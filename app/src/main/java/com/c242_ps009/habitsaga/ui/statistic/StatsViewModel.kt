package com.c242_ps009.habitsaga.ui.statistic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class StatsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _taskCounts = MutableLiveData<Pair<Int, Int>>()
    val taskCounts: LiveData<Pair<Int, Int>> get() = _taskCounts

    private val _taskDaily = MutableLiveData<List<Int>>()
    val taskDaily: LiveData<List<Int>> get() = _taskDaily

    private val _priorityCounts = MutableLiveData<Map<String, Int>>()
    val priorityCounts: LiveData<Map<String, Int>> get() = _priorityCounts

    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    fun fetchPriorityCounts() {
        val userId = getUserId() ?: return
        listenerRegistration = firestore.collection("eisenhower_tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StatsViewModel", "Error fetching priority counts", e)
                    _priorityCounts.postValue(emptyMap())
                    return@addSnapshotListener
                }

                val priorityMap = mutableMapOf(
                    "urgent important" to 0,
                    "urgent not-important" to 0,
                    "not-urgent important" to 0,
                    "not-urgent not-important" to 0
                )
                snapshot?.documents?.forEach { document ->
                    val priority = document.getString("priority") ?: "Unknown"
                    if (priority in priorityMap) {
                        priorityMap[priority] = priorityMap.getOrDefault(priority, 0) + 1
                    }
                }
                _priorityCounts.postValue(priorityMap)
            }
    }

    fun fetchTaskCounts() {
        val userId = getUserId() ?: return
        listenerRegistration = firestore.collection("eisenhower_tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StatsViewModel", "Error fetching task counts", e)
                    _taskCounts.postValue(Pair(0, 0))
                    return@addSnapshotListener
                }

                val completedTaskCount = snapshot?.documents?.count { it.getBoolean("isCompleted") == true } ?: 0
                val incompleteTaskCount = snapshot?.documents?.count { it.getBoolean("isCompleted") == false } ?: 0
                _taskCounts.postValue(Pair(completedTaskCount, incompleteTaskCount))
            }
    }

    fun fetchTaskDaily() {
        val userId = getUserId() ?: return
        listenerRegistration = firestore.collection("eisenhower_tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StatsViewModel", "Error fetching task counts", e)
                    _taskDaily.postValue(List(7) { 0 })
                    return@addSnapshotListener
                }

                val calendar = java.util.Calendar.getInstance()
                val dayOfWeekCounts = IntArray(7) { 0 }

                snapshot?.documents?.forEach { document ->
                    document.getDate("completedAt")?.let { completedAt ->
                        calendar.time = completedAt
                        val dayOfWeek = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                        dayOfWeekCounts[dayOfWeek]++
                    }
                }
                _taskDaily.postValue(dayOfWeekCounts.toList())
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}