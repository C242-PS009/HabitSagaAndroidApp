package com.c242_ps009.habitsaga.data.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/predict")
    fun getTaskPriorities(@Body request: TaskRequest): Call<TaskResponse>
}

data class TaskRequest(
    val tasks: List<String>
)

data class TaskResponse(
    val labels: List<String>
)
