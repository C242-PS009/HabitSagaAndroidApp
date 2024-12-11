package com.c242_ps009.habitsaga.ui.gamification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserInfo(userId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val userDoc = fetchUserFromFirestore(userId)

                if (userDoc.exists()) {
                    val username = userDoc.getString("name") ?: "No username"
                    val expPoints = userDoc.getLong("expPoints") ?: 0
                    val coin = userDoc.getLong("coin") ?: 0
                    val expProgress = (expPoints % 100).toInt()
                    val level = (expPoints / 100).toInt()

                    _userData.value = User(username, level, expPoints.toInt(), coin.toInt(), expProgress)

                } else {
                    _errorMessage.value = "User not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching data: ${e.message}"
                Log.e("UserViewModel", "Error fetching user info: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchUserFromFirestore(userId: String): DocumentSnapshot {
        return withContext(Dispatchers.IO) {
            firestore.collection("users").document(userId).get().await()
        }
    }
}

