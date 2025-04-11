

package com.example.xplorica.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LaunchARButton(context: Context, modelUrl: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                .buildUpon()
                .appendQueryParameter("file", modelUrl)
                .appendQueryParameter("mode", "ar_preferred")
                .build()

            val sceneViewerIntent = Intent(Intent.ACTION_VIEW).apply {
                data = intentUri
                setPackage("com.google.android.googlequicksearchbox") // Required for Scene Viewer
            }

            context.startActivity(sceneViewerIntent)
        },
        modifier = modifier
    ) {
        Text("View Dance in AR")
    }
}