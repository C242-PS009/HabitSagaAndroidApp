package com.c242_ps009.habitsaga.ui.shop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.c242_ps009.habitsaga.utils.Result
import kotlinx.coroutines.tasks.await

class ShopRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val shopItemsCollection: CollectionReference = firestore.collection(SHOP_ITEMS_COLLECTION)

//    fun fetchData(): LiveData<Result<List<PurchasableItem>>> = liveData {
//        Log.d(TAG, "Called")
//        emit(Result.Loading)
//        shopItemsCollection.get().addOnCompleteListener { shopItem ->
//            if (shopItem.isSuccessful) {
//                val documents = shopItem.result?.documents
//                documents?.forEach { document ->
//                    Log.d(TAG, "Document ID: ${document.id}, Data: ${document.data}")
//                }
//            } else {
//                Log.e(TAG, "Error getting documents: ", shopItem.exception)
//            }
//        }
//        val shopItemList: LiveData<Result<List<PurchasableItem>>> = liveData { shopItemsCollection.get() }
//        for (shopItem in shopItemList) {
//            Log.d(TAG, "Shop Item: $shopItem")
//        }
//        emitSource(shopItemList)
//    }

    fun fetchData(): LiveData<Result<List<PurchasableItem>>> = liveData {
        Log.d(TAG, "Called")
        emit(Result.Loading)
        try {
            val shopItemsSnapshot = shopItemsCollection.get().await() // Use await() for coroutines
            val shopItems = shopItemsSnapshot.documents.mapNotNull { document ->
                document.toObject(PurchasableItem::class.java)?.apply {
                    itemId = document.id
                }
            }
            Log.d(TAG, "Shop Items: $shopItems")
            emit(Result.Success(shopItems))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents: ", e)
            emit(Result.Error(e.toString()))
        }
    }

    suspend fun purchaseItem(userId: String, itemId: String, TimeStamp: com.google.firebase.Timestamp) {
        val purchasesCollection = firestore.collection("purchases")
        val purchases = Purchases(userId, itemId, TimeStamp.toDate())
        purchasesCollection.add(purchases.toMap()).await()
    }

    companion object {
        private const val TAG = "ShopRepository"
        private const val SHOP_ITEMS_COLLECTION = "items"
    }
}