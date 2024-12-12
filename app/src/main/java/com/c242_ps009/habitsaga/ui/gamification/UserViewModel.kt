package com.c242_ps009.habitsaga.ui.gamification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c242_ps009.habitsaga.ui.inventory.Inventory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private var userListener: ListenerRegistration? = null

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserInfo(userId: String) {
        _isLoading.value = true

        userListener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _errorMessage.value = "Error fetching data: ${e.message}"
                    _isLoading.value = false
                    Log.e("UserViewModel", "Error fetching user info: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val username = snapshot.getString("name") ?: "No username"
                    val expPoints = snapshot.getLong("expPoints") ?: 1
                    val coin = snapshot.getLong("coin") ?: 0
                    val expProgress = (expPoints % 100).toInt()
                    val level = (expPoints / 100).toInt() + 1
                    val equippedItemLayer1 = snapshot.getString("equippedItemLayer1") ?: ""
                    val equippedItemLayer2 = snapshot.getString("equippedItemLayer2") ?: ""

                    _userData.value = User(username, level, expPoints.toInt(), coin.toInt(), expProgress, equippedItemLayer1 = equippedItemLayer1, equippedItemLayer2 = equippedItemLayer2)
                    _isLoading.value = false
                } else {
                    _errorMessage.value = "User not found"
                    _isLoading.value = false
                }
            }
    }

    fun equipItem(item: Inventory) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        userListener = userRef.addSnapshotListener { document, e ->
            if (e != null) {
                Log.e("UserViewModel", "Error equipping item: ${e.message}")
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                val equippedItemLayer1 = if (item.itemCategory == "character") item.img else document.getString("equippedItemLayer1") ?: ""
                val equippedItemLayer2 = if (item.itemCategory == "equip") item.img else document.getString("equippedItemLayer2") ?: ""

                userRef.update(
                    mapOf(
                        "equippedItemLayer1" to equippedItemLayer1,
                        "equippedItemLayer2" to equippedItemLayer2
                    )
                ).addOnSuccessListener {
                    Log.d("UserViewModel", "Item equipped successfully")
                }.addOnFailureListener { e ->
                    Log.e("UserViewModel", "Error equipping item: ${e.message}")
                }
            }
        }
    }

    /*
     You know, Firestore handles the removal under the hood... but hey, I’m just here
     to make sure it's removed manually too, because memory leaks are a big no-no,
     and who doesn't love being extra cautious? Better safe than sorry!
  */
    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
    }
}