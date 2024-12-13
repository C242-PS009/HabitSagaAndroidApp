package com.c242_ps009.habitsaga.ui.shop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c242_ps009.habitsaga.ui.inventory.Inventory
import com.c242_ps009.habitsaga.ui.inventory.InventoryRepository
import com.c242_ps009.habitsaga.ui.inventory.InventoryViewModel
import com.c242_ps009.habitsaga.ui.inventory.InventoryViewModel.Companion
import com.c242_ps009.habitsaga.utils.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class ShopViewModel: ViewModel() {
    private val repo = ShopRepository()
    private val inventoryRepo = InventoryRepository()

    private val _shopItems = MutableLiveData<List<PurchasableItem>>()
    val shopItems: LiveData<List<PurchasableItem>> = _shopItems

    private val _inventoryItems = MutableLiveData<List<Inventory>>()
    val inventoryItems: LiveData<List<Inventory>> = _inventoryItems

    private val _inventoryResult = MutableLiveData<Result<List<Inventory>>>()
    val inventoryResult: LiveData<Result<List<Inventory>>> = _inventoryResult

    private val _resultState = MutableLiveData<Int>()
    val resultState: LiveData<Int> = _resultState

    init {
        fetchShopItems()
        fetchInventory()
    }

    private fun fetchShopItems() {
        _resultState.value = 0
        repo.fetchData().observeForever { result ->
            when (result) {
                is Result.Loading -> _resultState.value = 0
                is Result.Success -> {
                    _resultState.value = 1
                    _shopItems.value = result.data
                    Log.d(TAG, "fetchShopItems Success: ${result.data.size}")
                }
                is Result.Error -> {
                    _resultState.value = -1
                    Log.e(TAG, "fetchShopItems Error: ${result.error}")
                }
                else -> Log.e(TAG, "fetchShopItems Unknown result: $result")
            }
        }
    }

    private fun fetchInventory() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            _resultState.value = 0
            _inventoryResult.value = Result.Loading

            inventoryRepo.fetchInventory(userId)

            inventoryRepo.inventory.observeForever { result ->
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

    suspend fun purchaseItem(userId: String, itemId: String) {
        repo.purchaseItem(userId, itemId, Timestamp.now())
    }

    companion object {
        private const val TAG = "ShopViewModel"
    }
}