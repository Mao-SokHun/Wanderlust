package com.example.wanderlust.data

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun DestinationCard.resolvedGeo(): GeoLocation {
    if (latitude != null && longitude != null) {
        return GeoLocation(
            name = location.ifBlank { title },
            latitude = latitude,
            longitude = longitude,
        )
    }
    return geoForDestination(title, category)
}

/** Great-circle distance in kilometres. */
fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

data class NearbyDestination(
    val destination: DestinationCard,
    val distanceKm: Double,
)

fun destinationsNear(
    latitude: Double,
    longitude: Double,
    limit: Int = 6,
    excludeId: String? = null,
): List<NearbyDestination> =
    DestinationCatalog.allDestinations
        .asSequence()
        .filter { it.id != excludeId }
        .map { dest ->
            val geo = dest.resolvedGeo()
            NearbyDestination(
                destination = dest,
                distanceKm = distanceKm(latitude, longitude, geo.latitude, geo.longitude),
            )
        }
        .sortedBy { it.distanceKm }
        .take(limit)
        .toList()

fun formatDistanceKm(km: Double): String =
    if (km < 10) String.format("%.1f km", km) else String.format("%.0f km", km)

fun formatWalkMinutes(minutes: Int): String =
    if (minutes < 60) "~$minutes min walk" else "~${minutes / 60} h walk"
