package com.example.xplorica.ui

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.xplorica.ui.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detectionResult by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        bitmap = it
        it?.let { image ->
            uploadImageToServer(context, image) { result ->
                detectionResult = result
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { launcher.launch(null) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF8A2BE2)),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text("📷 Detect Landmark", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp)
            )
        }

        detectionResult?.let {
            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                backgroundColor = Color(0xFF4F4F4F),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF32CD32), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏛️", fontSize = MaterialTheme.typography.subtitle1.fontSize)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "✅ Result: $it",
                        color = Color.White,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

fun uploadImageToServer(context: Context, bitmap: Bitmap, onResult: (String) -> Unit) {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()

    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.211.1:8000/") // Make sure this matches your FastAPI server IP and port
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)

    api.uploadImage(multipartBody).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                val result = response.body()?.string() ?: "No response body"
                Toast.makeText(context, "✅ Result: $result", Toast.LENGTH_LONG).show()
                onResult(result)
            } else {
                val errorMsg = "Server error: ${response.code()}"
                Toast.makeText(context, "❌ $errorMsg", Toast.LENGTH_LONG).show()
                onResult(errorMsg)
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            val errorMsg = "Upload failed: ${t.message}"
            Toast.makeText(context, "❌ $errorMsg", Toast.LENGTH_LONG).show()
            onResult(errorMsg)
        }
    })
}