package com.example.wanderlust.data.model

data class ImageUploadResponse(
    val urls: List<String> = emptyList(),
    val count: Int = 0,
    val message: String? = null,
)
