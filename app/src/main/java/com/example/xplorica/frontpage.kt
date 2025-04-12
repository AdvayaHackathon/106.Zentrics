package com.example.xplorica

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@Composable
fun FrontPage(navController: NavController, modifier: Modifier = Modifier) {

    // 👇 Automatically navigate after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000) // Delay for 2000 milliseconds = 2 seconds
        navController.navigate("frontpage2") {
            popUpTo("front_page") { inclusive = true } // Optional: removes current screen from backstack
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.xplorica),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

