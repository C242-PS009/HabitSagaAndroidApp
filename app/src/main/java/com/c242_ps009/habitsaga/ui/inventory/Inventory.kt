package com.c242_ps009.habitsaga.ui.inventory

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inventory(
    @PropertyName("itemId") var itemId: String = "",
    @PropertyName("img") val img: String = "",
    @PropertyName("itemCategory") val itemCategory: String = "",
    @PropertyName("name") val name: String = ""
): Parcelable