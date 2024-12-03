package com.c242_ps009.habitsaga.ui.task

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tasksCollection: CollectionReference = firestore.collection("eisenhower_tasks")

    suspend fun addTask(task: Task): Result<String> {
        return try {
            val taskMap = hashMapOf(
                "userId" to task.userId,
                "id" to task.id,
                "title" to task.title,
                "description" to task.description,
                "dueDate" to task.dueDate,
                "category" to task.category,
                "isCompleted" to task.isCompleted,
                "priority" to task.priority
            )
            val documentReference = tasksCollection.add(taskMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error adding task", e)
            Result.failure(e)
        }
    }

    suspend fun updateTask(documentId: String, updatedTask: Task): Result<Unit> {
        return try {
            val taskMap = hashMapOf(
                "title" to updatedTask.title,
                "description" to updatedTask.description,
                "dueDate" to updatedTask.dueDate,
                "category" to updatedTask.category,
                "isCompleted" to updatedTask.isCompleted,
                "priority" to updatedTask.priority
            ) as Map<String, Any>
            val documentReference = tasksCollection.document(documentId)
            documentReference.update(taskMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(documentId: String): Result<Unit> {
        return try {
            val documentReference = tasksCollection.document(documentId)
            documentReference.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting task", e)
            Result.failure(e)
        }
    }

    fun observeTask(onTasksUpdated: (List<Task>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        return tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                onError(exception)
                return@addSnapshotListener
            }

            val tasks = mutableListOf<Task>()
            snapshot?.documents?.forEach { document ->
                val task = document.toObject(Task::class.java)
                task?.let {
                    it.id = document.id
                    tasks.add(it)
                } ?: Log.w("TaskRepository", "Document could not be mapped to Task")
            }

            onTasksUpdated(tasks)
        }
    }

    fun stopObserver(listenerRegistration: ListenerRegistration) {
        listenerRegistration.remove()
    }
}
