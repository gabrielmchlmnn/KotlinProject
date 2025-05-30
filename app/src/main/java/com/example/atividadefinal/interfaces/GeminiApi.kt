package com.example.atividadefinal.interfaces

import com.example.atividadefinal.data.model.GeminiRequest
import com.example.atividadefinal.data.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiApi {
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Header("Authorization") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
