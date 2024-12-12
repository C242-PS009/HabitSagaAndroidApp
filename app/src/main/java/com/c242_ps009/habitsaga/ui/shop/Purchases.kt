package com.c242_ps009.habitsaga.ui.shop

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Purchases(
    @PropertyName("userId") val userId: String = "",
    @PropertyName("itemId") val itemId: String = "",
    @ServerTimestamp @PropertyName("purchaseDate") var purchaseDate: Date? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "itemId" to itemId,
            "purchaseDate" to purchaseDate
        )
    }
}
