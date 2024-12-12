package com.c242_ps009.habitsaga.ui.shop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c242_ps009.habitsaga.utils.Result

class ShopViewModel: ViewModel() {
    private val repo = ShopRepository()

    private val _shopItems = MutableLiveData<List<PurchasableItem>>()
    val shopItems: LiveData<List<PurchasableItem>> = _shopItems

    private val _resultState = MutableLiveData<Int>()
    val resultState: LiveData<Int> = _resultState

    init { fetchShopItems() }

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

    companion object {
        private const val TAG = "ShopViewModel"
    }
}