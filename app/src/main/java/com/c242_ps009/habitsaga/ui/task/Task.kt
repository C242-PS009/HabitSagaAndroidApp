package com.c242_ps009.habitsaga.ui.task

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    @PropertyName("userId") val userId: String = "",
    @PropertyName("id") var id: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("dueDate") val dueDate: Date = Date(),
    @PropertyName("category") val category: String = "",
    @PropertyName("isCompleted") var isCompleted: Boolean = false,
    @PropertyName("priority") val priority: String = "",
    @PropertyName("deleted") val deleted: Boolean = false,
    @ServerTimestamp @PropertyName("completedAt") var completedAt: Date? = null
): Parcelable {

    /*
        Turning that Task into a map for Firestore because it's easier to store
        data in a structured format, and Firestore doesn't like Kotlin objects directly.
    */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "id" to id,
            "title" to title,
            "description" to description,
            "dueDate" to dueDate,
            "category" to category,
            "isCompleted" to isCompleted,
            "priority" to priority,
            "deleted" to deleted,
            "completedAt" to completedAt
        )
    }
}
