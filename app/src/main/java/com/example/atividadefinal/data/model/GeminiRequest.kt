package com.example.atividadefinal.data.model

import com.google.ai.client.generativeai.type.Content

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)