package com.c242_ps009.habitsaga.ui.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.c242_ps009.habitsaga.utils.Result

class InventoryRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val inventoryCollection = firestore.collection(INVENTORY_COLLECTION)

    private val _inventory = MutableLiveData<Result<List<Inventory>>>()
    val inventory: LiveData<Result<List<Inventory>>> get() = _inventory

    private var listenerRegistration: ListenerRegistration? = null

    fun fetchInventory(userId: String) {
        Log.d(TAG, "Started real-time listener with User ID: $userId")
        _inventory.value = Result.Loading
        listenerRegistration = inventoryCollection.document(userId).collection("inventory")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Error getting documents: ${exception.localizedMessage}", exception)
                    _inventory.value = Result.Error(exception.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val inventoryItems = snapshot.documents.mapNotNull { document ->
                        document.toObject(Inventory::class.java)?.apply { itemId = document.id }
                    }
                    _inventory.value = Result.Success(inventoryItems)
                } else {
                    Log.d(TAG, "No inventory data found.")
                    _inventory.value = Result.Success(emptyList())
                }
            }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
        Log.d(TAG, "Stopped real-time listener.")
    }

    companion object {
        private const val INVENTORY_COLLECTION = "users"
        private const val TAG = "InventoryRepository"
    }
}