package com.example.xplorica.model

// File: LandmarkResult.kt


data class LandmarkResult(
    val name: String,
    val confidence: Float,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?
)