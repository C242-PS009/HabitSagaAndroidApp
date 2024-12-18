package com.c242_ps009.habitsaga.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _taskSaveStatus = MutableLiveData<Result<String>>()
    val taskSaveStatus: LiveData<Result<String>> = _taskSaveStatus

    private val _taskDeleteStatus = MutableLiveData<Result<Unit>>()
    val taskDeleteStatus: LiveData<Result<Unit>> = _taskDeleteStatus

    private val _deleteAllTasksStatus = MutableLiveData<Result<Unit>>()
    val deleteAllTasksStatus: LiveData<Result<Unit>> = _deleteAllTasksStatus

    private val _taskUpdateStatus = MutableLiveData<Result<Unit>>()
    val taskUpdateStatus: LiveData<Result<Unit>> = _taskUpdateStatus

    val tasksLiveData = MutableLiveData<List<Task>?>()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var listenerRegistration: ListenerRegistration? = null
    init {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            startRealTimeUpdates(userId)
        } else {
            _error.value = "User not authenticated"
        }
    }

    // Add a task to Firestore, what do you expect?
    fun addNewTask(id: String, title: String, description: String, dueDate: String, category: String, isCompleted: Boolean) {
        _loading.value = true
        viewModelScope.launch {
            val parsedDueDate = parseDate(dueDate)
            val task = Task(
                userId = id,
                title = title,
                description = description,
                dueDate = parsedDueDate ?: Date(),
                category = category,
                isCompleted = isCompleted
            )

            val result = taskRepository.addTask(task)
            _taskSaveStatus.value = result

            if (result.isFailure) {
                _error.value = "Error adding task: ${result.exceptionOrNull()?.message}"
            }
            _loading.value = false
        }
    }

    // Real-time updates from Firestore, fetch/read tasks in a nutshell
    private fun startRealTimeUpdates(userId: String) {
        listenerRegistration = taskRepository.startListener(
            userId = userId,
            onTasksUpdated = { tasks ->
                tasksLiveData.value = tasks.filter { !it.deleted }
            },
            onError = { exception ->
                _error.value = "Error receiving real-time updates: ${exception.message}"
            }
        )
    }

    // Update a task in Firestore, what do you expect?
    fun updateTask(documentId: String, updatedTask: Task) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = taskRepository.updateTask(documentId, updatedTask)
                _taskUpdateStatus.value = result

                if (result.isFailure) {
                    _error.value = "Error updating task: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error updating task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Delete a task in Firestore, what do you expect?
    fun deleteTask(documentId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = taskRepository.deleteTask(documentId)
                _taskDeleteStatus.value = result

                if (result.isSuccess) {
                    tasksLiveData.value = tasksLiveData.value?.filter { it.id != documentId }
                } else {
                    _error.value = "Error deleting task: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Function to delete all tasks
    fun deleteAllTasks() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = taskRepository.deleteAllTasks()
                _deleteAllTasksStatus.value = result

                if (result.isSuccess) {
                    tasksLiveData.value = emptyList()
                } else {
                    _error.value = "Error deleting all tasks: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting all tasks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun markTaskCompleted(taskId: String) {
        val result = taskRepository.completedTask(taskId)
        result.onSuccess {
            Log.d("TaskRepository", "Task marked as completed")
        }.onFailure { exception ->
            Log.e("TaskRepository", "Error marking task as completed", exception)
        }
    }

    /*
       You know, Firestore handles the removal under the hood... but hey, I’m just here
       to make sure it's removed manually too, because memory leaks are a big no-no,
       and who doesn't love being extra cautious? Better safe than sorry!
    */
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun fetchAndProcessTasks() {
        viewModelScope.launch {
            taskRepository.fetchAndProcessTasks()
        }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error parsing date: ${e.message}")
            null
        }
    }
}
