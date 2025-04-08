package com.example.atividadefinal.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("menu", Icons.Default.Home, "Página Inicial")
    object NewTrip : BottomNavItem("new_trip", Icons.Default.Add, "Nova Viagem")
    object About : BottomNavItem("about", Icons.Default.Info, "Sobre")
}

