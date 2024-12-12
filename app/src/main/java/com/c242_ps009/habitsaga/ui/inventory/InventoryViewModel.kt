package com.c242_ps009.habitsaga.ui.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.c242_ps009.habitsaga.utils.Result

class InventoryViewModel: ViewModel() {
    private val repository = InventoryRepository()

    private val _inventoryItems = MutableLiveData<List<Inventory>>()
    val inventoryItems: LiveData<List<Inventory>> = _inventoryItems

    private val _inventoryResult = MutableLiveData<Result<List<Inventory>>>()
    val inventoryResult: LiveData<Result<List<Inventory>>> = _inventoryResult

    private val _resultState = MutableLiveData<Int>()
    val resultState: LiveData<Int> = _resultState

    init {
        fetchInventory()
    }

    private fun fetchInventory() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            _resultState.value = 0
            _inventoryResult.value = Result.Loading

            repository.fetchInventory(userId)

            repository.inventory.observeForever { result ->
                when (result) {
                    is Result.Loading -> {
                        _resultState.value = 0
                        _inventoryResult.value = result
                    }
                    is Result.Success -> {
                        _inventoryItems.value = result.data
                        _resultState.value = 1
                        _inventoryResult.value = result
                        Log.d(TAG, "fetchInventory Success: ${result.data.size}")
                    }
                    is Result.Error -> {
                        _resultState.value = -1
                        _inventoryResult.value = result
                        Log.e(TAG, "fetchInventory Error: ${result.error}")
                    }
                }
            }
        } else {
            Log.e(TAG, "No user is signed in")
            _resultState.value = -1
            _inventoryResult.value = Result.Error("No user signed in")
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
        Log.d(TAG, "InventoryViewModel cleared and listener stopped.")
    }

    companion object {
        private const val TAG = "InventoryViewModel"
    }
}