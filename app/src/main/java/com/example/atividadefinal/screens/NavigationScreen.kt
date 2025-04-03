package com.example.atividadefinal.screens


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.atividadefinal.screens.LoginScreen
import com.example.atividadefinal.screens.RegisterScreen
import com.example.atividadefinal.screens.MenuScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("menu") { MenuScreen() }
    }
}
