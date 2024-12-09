package com.c242_ps009.habitsaga.ui.task

import android.util.Log
import com.c242_ps009.habitsaga.data.retrofit.ApiConfig
import com.c242_ps009.habitsaga.data.retrofit.TaskRequest
import com.c242_ps009.habitsaga.data.retrofit.TaskResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
            }?.filter { !it.deleted } ?: emptyList()
            onTasksUpdated(tasks)
        }
    }

    suspend fun getTasksByTitle(title: String): Result<List<Task>> {
        return try {
            val tasks = tasksCollection
                .whereEqualTo("title", title)
                .whereEqualTo("isCompleted", false)
                .get()
                .await()
            val taskList = tasks.documents.mapNotNull { document ->
                document.toObject(Task::class.java)?.apply {
                    id = document.id
                }
            }
            Result.success(taskList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks with title: $title", e)
            Result.failure(e)
        }
    }

    fun fetchTitlesAndPredict() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getTasksByTitle("title")
            withContext(Dispatchers.Main) {
                result.onSuccess { titles ->
                    if (titles.isNotEmpty()) {
                        val request = TaskRequest(tasks = titles)

                        ApiConfig.apiService().getTaskPriorities(request).enqueue(object : Callback<TaskResponse> {
                            override fun onResponse(
                                call: Call<TaskResponse>,
                                response: Response<TaskResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val labels = response.body()?.labels
                                    labels?.let {
                                        Log.d("MLPrediction", "Predictions: $it")
                                    }
                                } else {
                                    Log.e("MLPrediction", "API Error: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                                Log.e("MLPrediction", "API Failure: ${t.message}")
                            }
                        })
                    } else {
                        Log.d("Firestore", "No titles found in Firestore.")
                    }
                }.onFailure { exception ->
                    Log.e("Firestore", "Error fetching titles: ", exception)
                }
            }
        }
    }

    suspend fun completedTask(documentId: String): Result<Unit> {
        return try {
            tasksCollection.document(documentId).update("isCompleted", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking task as completed with ID: $documentId", e)
            Result.failure(e)
        }
    }
}