package com.example.wanderlust.data.model

data class FavoriteRequest(
    val tourId: String,
    val title: String? = null,
)

data class CustomPlaceInput(
    val title: String,
    val location: String,
    val notes: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val mapsLink: String = "",
    /** Stable id so re-saving the same place updates instead of duplicating. */
    val preferredId: String? = null,
)
