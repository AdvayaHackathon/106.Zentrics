

package com.example.xplorica.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun LaunchARButton(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                .buildUpon()
                .appendQueryParameter("mode", "ar_preferred")
                .build()

            val sceneViewerIntent = Intent(Intent.ACTION_VIEW).apply {
                data = intentUri
                setPackage("com.google.android.googlequicksearchbox") // Required for Scene Viewer
            }

            // Try to open the Scene Viewer intent
            try {
                // Check if there is an app that can handle this intent
                if (sceneViewerIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(sceneViewerIntent)
                } else {
                    // Show error if no app can handle the intent
                    Toast.makeText(context, "Scene Viewer is not installed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle any exceptions or crashes
                Toast.makeText(context, "Error launching AR: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    ) {
        Text("Launch Default AR")
    }
}