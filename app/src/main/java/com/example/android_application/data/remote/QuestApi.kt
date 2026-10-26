package com.example.android_application.data.remote

import com.example.android_application.data.remote.dto.ScenarioDto
import retrofit2.http.GET

interface QuestApi {
    @GET("api/users")
    suspend fun getScenarios(): List<ScenarioDto>
}