package com.example.atividadefinal.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

suspend fun generateSuggestionFromGemini(cidade: String, dataFinal: String, dataInicio: String, tipo: String): String {
    val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyBBddS3SdxTHO4oLd7sRoj2LTY5uoFda9E"
    )
    val prompt = content {
        text("Monte um roteiro de viagem com pontos turísticos e atividades para a cidade de $cidade entre os dias $dataInicio e $dataFinal. Quero que sugira pontos de uma viagem voltada para $tipo")
    }

    val response = generativeModel.generateContent(prompt)

    return response.text ?: "Não foi possível gerar uma sugestão no momento."
}

