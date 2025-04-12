import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.xplorica.BottomNavBar
import com.example.xplorica.FactsScreen
import com.example.xplorica.FrontPage
import com.example.xplorica.FrontPage2
import com.example.xplorica.HomeScreen
import com.example.xplorica.Mainmenu
import com.example.xplorica.ui.LaunchARButton
import com.example.xplorica.ui.SearchScreen

@Composable
fun Nav(navController: NavHostController) {


    // Observe the current back stack entry to know the current route
    val currentDestination = remember { mutableStateOf<String?>(null) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentDestination.value = destination.route
    }

    val showBottomBar = currentDestination.value in listOf("home", "ar_screen", "facts")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "front",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("front") { FrontPage(navController) }
            composable("frontpage2") { FrontPage2(navController) }
            composable("menu") { Mainmenu(navController) }

            // Bottom bar screens
            composable("home") { HomeScreen(navController) }
            composable("ar_screen") {
                val context = LocalContext.current
                LaunchARButton(
                    navController = navController,
                    context = context,
                    modelUrl = "https://modelviewer.dev/shared-assets/models/Astronaut.glb" // Demo model
                )
            }
            composable("search") { SearchScreen(navController) }
        }
    }
}