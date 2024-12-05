package com.c242_ps009.habitsaga.ui.gamification

import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("name") val name: String = "No username",
    @PropertyName("level") val level: Int = 0,
    @PropertyName("expPoints") val expPoints: Int = 0,
    @PropertyName("coin") val coin: Int = 0,
    val expProgress : Int
)