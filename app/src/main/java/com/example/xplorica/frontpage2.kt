package com.example.xplorica

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

import android.graphics.Point
import android.location.Location
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.border

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.overlay.Marker
import androidx.core.content.res.ResourcesCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import android.app.Activity
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper

import android.util.Log

import okhttp3.*
import org.osmdroid.views.overlay.Overlay


@Composable
fun FrontPage2() {
    val Beige = Color(0xFFFFFF)
    val AlataFont = FontFamily(
        Font(R.font.alata)
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
    ) {
        // Top bar (Menu + Profile)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 45.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menu",
                modifier = Modifier
                    .size(25.dp)
                    .clickable { /* Handle menu click */ }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 🔍 Search Bar
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                AnimatedSearchText()
            }
            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { /* Handle profile click */ },
                contentScale = ContentScale.Crop // 👈 ensures it fills the circle properly
            )
        }

        Text(
            text = "UNESCO SITES",
            style = TextStyle(
                fontSize = 18.sp, // 👈 Adjust this size as needed
                fontFamily = AlataFont,
                color = Color(0xFF003366)
            ),
            modifier = Modifier.padding(start = 25.dp, top = 155.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SwipeableAutoSlidingCarousel()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .padding(horizontal = 20.dp, vertical = 30.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                OsmMapView(modifier = Modifier.fillMaxSize())
            }
        }


        // Bottom Navigation Bar
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomNavBar()
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar() {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Search", Icons.Default.Search),
        BottomNavItem("Facts", Icons.Default.Edit)
    )

    var selectedItem by remember { mutableStateOf(0) }
    val BottomBarColor = Color(0xFFEDF1FC)

    NavigationBar(
        containerColor = BottomBarColor
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}



@OptIn(ExperimentalPagerApi::class)
@Composable
fun SwipeableAutoSlidingCarousel() {
    val allBoxes = listOf(
        Color(0xFF2B2D42),
        Color(0xFF3E1F47),
        Color(0xFF1E3D59),
        Color(0xFF44355B),
        Color(0xFF0D1B2A),
        Color(0xFF2F4858)
    )

    // Sample image resources (replace with your actual images)
    val images = listOf(
        R.drawable.agrafort,
        R.drawable.hampi,
        R.drawable.qutubminar,
        R.drawable.suntemple,
        R.drawable.tajmahal,
        R.drawable.elloracaves
    )

    val boxTexts = listOf(
        "Agra Fort",
        "Hampi",
        "Qutub\nMinar",
        "Sun\nTemple",
        "TajMahal",
        "Ellora\nCaves"
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val srirachafont = FontFamily(
        Font(R.font.sriracha)  // Use the exact filename without extension
    )


    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % (allBoxes.size / 2)
            coroutineScope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            count = allBoxes.size / 2,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 20.dp)
        ) { page ->
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(165.dp)
                        .fillMaxSize()
                        .fillMaxHeight()
                        .background(allBoxes[page * 2], RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = boxTexts[page * 2],
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = srirachafont),

                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(70.dp) // Outer circle size
                            .align(Alignment.CenterEnd)
                            .offset(x = (-2).dp) // Adjust position to the left
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = images[page * 2]),
                            contentDescription = "Circle Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp) // Inner image size
                                .clip(CircleShape)
                        )
                    }
                }
                Box(

                    modifier = Modifier
                        .width(165.dp)
                        .fillMaxSize()
                        .fillMaxHeight()
                        .background(allBoxes[page * 2+1], RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = boxTexts[page * 2 + 1],
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = srirachafont),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(70.dp) // Outer circle size
                            .align(Alignment.CenterEnd)
                            .offset(x = (-2).dp) // Adjust position to the left
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = images[page * 2 + 1]),
                            contentDescription = "Circle Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp) // Inner image size
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.Black,
            inactiveColor = Color.LightGray,
            indicatorWidth = 24.dp,
            indicatorHeight = 3.dp,
            spacing = 8.dp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun OsmMapView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_location_smooth)

    AndroidView(
        factory = {
            // Load configuration
            Configuration.getInstance().load(
                context,
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
            )

            // Setup map view
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            val controller = mapView.controller
            controller.setZoom(6.0)

            // Convert drawable to bitmap
            val bitmap = drawable?.let {
                Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888).also { bmp ->
                    val canvas = Canvas(bmp)
                    it.setBounds(0, 0, canvas.width, canvas.height)
                    it.draw(canvas)
                }
            }

            // Setup current location overlay
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
                enableMyLocation()
                enableFollowLocation()
                isDrawAccuracyEnabled = true

                bitmap?.let { setPersonIcon(it) }

                runOnFirstFix {
                    mapView.post {
                        val loc = myLocation
                        controller.setZoom(13.0)
                        controller.setCenter(loc)
                        controller.animateTo(loc)
                        bitmap?.let { setPersonIcon(it) }
                    }
                }
            }

            mapView.overlays.add(locationOverlay)

            // Fetch markers from Supabase
            fetchLocationsFromSupabase(context, mapView) { locations ->
                locations.forEach { (name, point) ->
                    val marker = Marker(mapView).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_location_smooth)?.apply {
                            setTint(android.graphics.Color.BLUE)
                        }
                    }

                    mapView.overlays.add(marker)
                    val labelOverlay = object : Overlay() {
                        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
                            if (shadow) return

                            val screenPoint = mapView.projection.toPixels(point, null)

                            val paint = Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 18f
                                isAntiAlias = true
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                textAlign = Paint.Align.CENTER
                                val alata = ResourcesCompat.getFont(context, R.font.alata)
                                typeface = alata ?: Typeface.DEFAULT_BOLD // fallback
                            }

                            canvas.drawText(name, screenPoint.x.toFloat(), screenPoint.y.toFloat() - 70f, paint)
                        }
                    }

                    mapView.overlays.add(labelOverlay)
                }
                mapView.invalidate()
            }

            mapView
        },
        modifier = modifier
    )
}

fun fetchLocationsFromSupabase(
    context: Context,
    mapView: MapView,
    onLocationsReady: (List<Pair<String, GeoPoint>>) -> Unit
) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://fyfevkzcuzuqpiamtufx.supabase.co/rest/v1/locations?select=*") // make sure to include select=*
        .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ5ZmV2a3pjdXp1cXBpYW10dWZ4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4NDc0NzYsImV4cCI6MjA1OTQyMzQ3Nn0.soEIZbCj-dJKsSxpnNk7DXa-By2mYRJ2rq4IALapqBo") // use service_role if RLS is ON
        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ5ZmV2a3pjdXp1cXBpYW10dWZ4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4NDc0NzYsImV4cCI6MjA1OTQyMzQ3Nn0.soEIZbCj-dJKsSxpnNk7DXa-By2mYRJ2rq4IALapqBo")
        .addHeader("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Supabase", " Network error: ${e.localizedMessage}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("Supabase", " Raw response: $responseBody")

            if (!response.isSuccessful || responseBody == null) {
                Log.e("Supabase", " Failed response: ${response.code}")
                return
            }

            try {
                val locations = mutableListOf<Pair<String, GeoPoint>>()
                val jsonArray = JSONArray(responseBody)

                if (jsonArray.length() == 0) {
                    Log.w("Supabase", " No locations returned from Supabase.")
                }

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    // Safely parse values
                    val name = item.optString("name", "Unknown")
                    val lat = item.optDouble("latitude", Double.NaN)
                    val lon = item.optDouble("longitude", Double.NaN)

                    if (!lat.isNaN() && !lon.isNaN()) {
                        Log.d("Supabase", " Location found: $name @ ($lat, $lon)")
                        locations.add(name to GeoPoint(lat, lon))
                    } else {
                        Log.w("Supabase", " Invalid lat/lon at index $i")
                    }
                }

                (context as Activity).runOnUiThread {
                    Log.i("Supabase", "🚀 Final list size: ${locations.size}")
                    Handler(Looper.getMainLooper()).postDelayed({
                        onLocationsReady(locations)
                    }, 2000)
                }

            } catch (e: Exception) {
                Log.e("Supabase", " JSON parsing error: ${e.localizedMessage}")
            }
        }
    })
}

@Composable
fun AnimatedSearchText() {
    var showSearch by remember { mutableStateOf(false) }
    val srirachafont = FontFamily(Font(R.font.sriracha)) // Replace with your font file

    // Auto-toggle every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            showSearch = !showSearch
        }
    }

    // The Box (your search bar placeholder)
    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedContent(
                targetState = showSearch,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = slideInVertically { height -> height } + fadeIn(),
                        initialContentExit = slideOutVertically { height -> -height } + fadeOut(),
                        targetContentZIndex = 1f
                    )
                },
                label = "SearchTextSlide"
            ) { state ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state) "Search..." else "Xplorica",
                        color = Color(0xFF003366),
                        fontSize = 14.sp,
                        fontFamily = srirachafont,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewFrontPage2() {
    FrontPage2()
}