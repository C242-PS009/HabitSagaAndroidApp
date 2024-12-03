package com.c242_ps009.habitsaga.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _taskSaveStatus = MutableLiveData<Result<String>>()
    val taskSaveStatus: LiveData<Result<String>> = _taskSaveStatus

    private val _taskDeleteStatus = MutableLiveData<Result<Unit>>()
    val taskDeleteStatus: LiveData<Result<Unit>> = _taskDeleteStatus

    private val _taskUpdateStatus = MutableLiveData<Result<Unit>>()
    val taskUpdateStatus: LiveData<Result<Unit>> = _taskUpdateStatus

    val tasksLiveData = MutableLiveData<List<Task>?>()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    internal var listenerRegistration: ListenerRegistration? = null

    init {
        startRealTimeUpdates()
    }

    fun addNewTask(id: String, title: String, description: String, dueDate: String, category: String, isCompleted: Boolean, priority: Int) {
        viewModelScope.launch {
            val parsedDueDate = parseDate(dueDate)
            val task = Task(
                userId = id,
                title = title,
                description = description,
                dueDate = parsedDueDate ?: Date(),
                category = category,
                isCompleted = isCompleted,
                priority = priority
            )

            val result = taskRepository.addTask(task)
            _taskSaveStatus.value = result

            if (result.isSuccess) {
                // Real-time listener will handle the update, i hope so
            } else {
                _error.value = "Error adding task: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    // Real-time updates from Firestore, fetch tasks in a nutshell
    private fun startRealTimeUpdates() {
        listenerRegistration = taskRepository.observeTask(
            onTasksUpdated = { tasks ->
                tasksLiveData.value = tasks
            },
            onError = { exception ->
                _error.value = "Error receiving real-time updates: ${exception.message}"
            }
        )
    }

    fun updateTask(documentId: String, updatedTask: Task) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = taskRepository.updateTask(documentId, updatedTask)
                _taskUpdateStatus.value = result

                if (result.isSuccess) {
                    // Real-time listener will handle the update, i hope so
                } else {
                    _error.value = "Error updating task: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error updating task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTask(documentId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = taskRepository.deleteTask(documentId)
                _taskDeleteStatus.value = result

                if (result.isFailure) {
                    _error.value = "Error deleting task: ${result.exceptionOrNull()?.message}"
                } else {
                    // Real-time listener will handle the deletion or something else, i hope so
                }
            } catch (e: Exception) {
                _error.value = "Error deleting task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US)
            dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error parsing date: ${e.message}")
            null
        }
    }
}
