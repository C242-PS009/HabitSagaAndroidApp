package com.c242_ps009.habitsaga.ui.task

import android.util.Log
import com.c242_ps009.habitsaga.data.retrofit.ApiConfig
import com.c242_ps009.habitsaga.data.retrofit.TaskRequest
import com.c242_ps009.habitsaga.data.retrofit.TaskResponse
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tasksCollection: CollectionReference = firestore.collection(TASKS_COLLECTION)

    companion object {
        private const val TASKS_COLLECTION = "eisenhower_tasks"
        private const val TAG = "TaskRepository"
    }

    // Add a task to Firestore, what do you expect?
    suspend fun addTask(task: Task): Result<String> {
        return try {
            val taskMap = Task(
                userId = task.userId,
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                category = task.category,
                isCompleted = task.isCompleted,
                priority = task.priority,
                deleted = task.deleted
            ).toMap()
            val documentReference = tasksCollection.add(taskMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding task: ${task.title}", e)
            Result.failure(e)
        }
    }

    // Update a task in Firestore, what do you expect?
    suspend fun updateTask(documentId: String, updatedTask: Task): Result<Unit> {
        return try {
            val taskMap = Task(
                userId = updatedTask.userId,
                title = updatedTask.title,
                description = updatedTask.description,
                dueDate = updatedTask.dueDate,
                category = updatedTask.category,
                isCompleted = updatedTask.isCompleted,
                priority = updatedTask.priority
            ).toMap()
            tasksCollection.document(documentId).update(taskMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task: ${updatedTask.title}", e)
            Result.failure(e)
        }
    }

    // Delete a task in Firestore, what do you expect?
    suspend fun deleteTask(documentId: String): Result<Unit> {
        return try {
            tasksCollection.document(documentId).update("deleted", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task with ID: $documentId", e)
            Result.failure(e)
        }
    }

    // Make a guess, what does this function do?
    suspend fun deleteAllTasks(): Result<Unit> {
        return try {
            val tasks = tasksCollection.get().await()
            for (document in tasks.documents) {
                tasksCollection.document(document.id).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all tasks", e)
            Result.failure(e)
        }
    }

    private val priorityMap = mapOf(
        "urgent important" to 1,
        "urgent not-important" to 2,
        "not-urgent important" to 3,
        "not-urgent not-important" to 4
    )

    // Real-time updates from Firestore, fetch/read tasks in a nutshell
    fun startListener(onTasksUpdated: (List<Task>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        return tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                onError(exception)
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.mapNotNull { document ->
                document.toObject(Task::class.java)?.apply {
                    id = document.id
                }
            }?.filter { !it.deleted }?.sortedBy { task ->
                priorityMap[task.priority] ?: Int.MAX_VALUE
            } ?: emptyList()
            onTasksUpdated(tasks)
        }
    }

    suspend fun completedTask(documentId: String): Result<Unit> {
        return try {
            tasksCollection.document(documentId).update(
                mapOf(
                    "isCompleted" to true,
                    "deleted" to true
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking task as completed with ID: $documentId", e)
            Result.failure(e)
        }
    }

    fun fetchAndProcessTasks() {
        tasksCollection
            .whereEqualTo("isCompleted", false)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tasks = task.result!!.documents.mapNotNull { document ->
                        document.id to document.getString("title")
                    }

                    val taskTitles = tasks.mapNotNull { it.second }

                    if (taskTitles.isNotEmpty()) {
                        val taskRequest = TaskRequest(taskTitles)
                        sendToMlApiAndUpdateTasks(tasks, taskRequest)
                    } else {
                        Log.e(TAG, "No task titles found")
                    }
                } else {
                    Log.e(TAG, "Error getting tasks: ", task.exception)
                }
            }
    }

    private fun sendToMlApiAndUpdateTasks(tasks: List<Pair<String, String?>>, taskRequest: TaskRequest) {
        ApiConfig.apiService().getTaskPriorities(taskRequest).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    val labels = response.body()?.labels
                    if (labels != null && labels.size == tasks.size) {
                        updateTaskPrioritiesInBatch(tasks, labels)
                    } else {
                        Log.e(TAG, "Mismatch between number of tasks and labels from API")
                    }
                } else {
                    Log.e(TAG, "API error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Log.e(TAG, "API failure: ${t.message}", t)
            }
        })
    }

    private fun updateTaskPrioritiesInBatch(tasks: List<Pair<String, String?>>, labels: List<String>) {
        val batch = firestore.batch()

        tasks.forEachIndexed { index, (documentId, _) ->
            val priority = labels.getOrNull(index)
            priority?.let {
                val taskRef = tasksCollection.document(documentId)
                batch.update(taskRef, "priority", it)
            }
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "All task priorities updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating priorities in batch", e)
            }
    }
}