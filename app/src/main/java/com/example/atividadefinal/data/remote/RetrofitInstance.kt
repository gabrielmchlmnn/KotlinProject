package com.example.atividadefinal.data.remote

import com.example.atividadefinal.interfaces.GeminiApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.gemini.yourprovider.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }
}

