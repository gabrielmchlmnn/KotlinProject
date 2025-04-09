package com.example.atividadefinal.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Sobre",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sobre o App de Viagens",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "O nosso aplicativo foi desenvolvido para ajudar você a planejar e gerenciar suas viagens de forma simples e eficiente. " +
                    "Com ele, você pode cadastrar viagens de lazer ou negócios, controlar datas de início e término, definir o orçamento e manter todas as informações organizadas em um só lugar.\n\n" +
                    "Nosso objetivo é facilitar o planejamento para que você aproveite cada momento da sua viagem, sem preocupações. " +
                    "Viaje tranquilo, sabendo que todos os detalhes estão sempre ao seu alcance!",
            fontSize = 16.sp,
            color = Color.Gray,
            lineHeight = 22.sp
        )
    }
}
