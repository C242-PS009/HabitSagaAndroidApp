package com.c242_ps009.habitsaga.ui.gamification

import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("name") val name: String = "No username",
    val level: Int = 1,
    @PropertyName("expPoints") val expPoints: Int = 0,
    @PropertyName("coin") val coin: Int = 0,
    val expProgress : Int,
    @PropertyName("equippedItemLayer1") var equippedItemLayer1: String = "",
    @PropertyName("equippedItemLayer2") var equippedItemLayer2: String = ""
)