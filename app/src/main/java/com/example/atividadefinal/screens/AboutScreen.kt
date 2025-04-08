package com.example.atividadefinal.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sobre o App de Viagens", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Este aplicativo permite cadastrar e gerenciar viagens de lazer ou negócios.")
    }
}
