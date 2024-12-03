package com.c242_ps009.habitsaga.ui.task

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Task(
    @PropertyName("userId") val userId: String = "",
    @PropertyName("id") var id: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("dueDate") val dueDate: Date = Date(),
    @PropertyName("category") val category: String = "",
    @PropertyName("isCompleted") val isCompleted: Boolean = false,
    @PropertyName("priority") val priority: Int? = null
)

