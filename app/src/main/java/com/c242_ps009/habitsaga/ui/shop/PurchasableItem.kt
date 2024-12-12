package com.c242_ps009.habitsaga.ui.shop

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchasableItem(
    @PropertyName("img") val img: String = "",
    @PropertyName("itemCategory") val itemCategory: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("price") val price: Long = 0,
    @PropertyName("requiredLevel") val requiredLevel: Long = 0
): Parcelable
