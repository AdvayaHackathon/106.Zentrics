package com.example.xplorica

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Nav(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "frontpage") {
        composable("frontpage") {
            FrontPage()
        }
    }
}