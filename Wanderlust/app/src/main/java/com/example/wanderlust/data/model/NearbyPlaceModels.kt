package com.example.wanderlust.data.model

import kotlin.math.roundToInt

data class NearbyPlace(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double? = null,
    val primaryType: String = "",
    val distanceKm: Double? = null,
    val openNow: Boolean? = null,
    val userRatingsTotal: Int? = null,
    val phoneNumber: String? = null,
    val hasPhoto: Boolean = false,
) {
    /** Approximate walking minutes at ~5 km/h. */
    val walkMinutes: Int?
        get() = distanceKm?.let { km ->
            (km * 12.0).roundToInt().coerceAtLeast(1)
        }
}

/** One-tap “what I need right now” shortcut. */
data class NearbyMoment(
    val id: String,
    val categoryLabel: String? = null,
    val openNowOnly: Boolean = false,
    val keyword: String = "",
)

/** UI category → Google Places type (null = broad nearby mix). */
data class NearbyPlaceCategory(
    val label: String,
    val googleType: String?,
    val isQuickNeed: Boolean = false,
)

enum class NearbySortMode {
    Nearest,
    TopRated,
}

object NearbyPlaceCategories {
    val all = listOf(
        NearbyPlaceCategory("Cafe", "cafe", isQuickNeed = true),
        NearbyPlaceCategory("Restaurant", "restaurant", isQuickNeed = true),
        NearbyPlaceCategory("ATM", "atm", isQuickNeed = true),
        NearbyPlaceCategory("Pharmacy", "pharmacy", isQuickNeed = true),
        NearbyPlaceCategory("Gas", "gas_station", isQuickNeed = true),
        NearbyPlaceCategory("Store", "store", isQuickNeed = true),
        NearbyPlaceCategory("Supermarket", "supermarket"),
        NearbyPlaceCategory("Hotel", "lodging"),
        NearbyPlaceCategory("Hospital", "hospital"),
        NearbyPlaceCategory("Bank", "bank"),
        NearbyPlaceCategory("Park", "park"),
        NearbyPlaceCategory("Gym", "gym"),
        NearbyPlaceCategory("Bakery", "bakery"),
        NearbyPlaceCategory("Bar", "bar"),
    )

    val quickNeeds: List<NearbyPlaceCategory> = all.filter { it.isQuickNeed }

    val moreCategories: List<NearbyPlaceCategory> = all.filterNot { it.isQuickNeed }

    /** Used when filter = All — many nearby business types. */
    val broadTypes: List<String> = listOf(
        "restaurant", "cafe", "bakery", "bar", "meal_takeaway",
        "store", "supermarket", "shopping_mall", "convenience_store",
        "lodging", "gas_station", "atm", "bank",
        "pharmacy", "hospital", "park", "tourist_attraction",
        "gym", "laundry", "parking", "car_repair",
    )

    val radiusOptionsMeters: List<Int> = listOf(500, 1000, 2000)

    fun momentsForHour(hourOfDay: Int = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)): List<NearbyMoment> {
        val timed = when (hourOfDay) {
            in 5..10 -> listOf(
                NearbyMoment(id = "morning_cafe", categoryLabel = "Cafe"),
                NearbyMoment(id = "morning_bakery", categoryLabel = "Bakery"),
            )
            in 11..15 -> listOf(
                NearbyMoment(id = "midday_food", categoryLabel = "Restaurant"),
                NearbyMoment(id = "midday_atm", categoryLabel = "ATM"),
            )
            in 16..21 -> listOf(
                NearbyMoment(id = "evening_food", categoryLabel = "Restaurant"),
                NearbyMoment(id = "evening_pharmacy", categoryLabel = "Pharmacy"),
            )
            else -> listOf(
                NearbyMoment(id = "late_convenience", categoryLabel = "Store"),
                NearbyMoment(id = "late_gas", categoryLabel = "Gas"),
            )
        }
        return timed + listOf(
            NearbyMoment(id = "open_now", openNowOnly = true),
            NearbyMoment(id = "emergency", categoryLabel = "Hospital"),
        )
    }

    /** Higher is better for a quick decision (open + rating − walk). */
    fun decisionScore(place: NearbyPlace): Double {
        val walk = place.walkMinutes?.toDouble() ?: 40.0
        val ratingBonus = (place.rating ?: 3.5) * 10.0
        val openBonus = when (place.openNow) {
            true -> 30.0
            false -> -50.0
            null -> 8.0
        }
        return openBonus + ratingBonus - walk
    }

    fun pickBest(places: List<NearbyPlace>): NearbyPlace? {
        if (places.isEmpty()) return null
        val preferOpen = places.filter { it.openNow != false }
        val pool = preferOpen.ifEmpty { places }
        return pool.maxByOrNull(::decisionScore)
    }

    fun bestPickReasonKeyParts(place: NearbyPlace): BestPickParts {
        val openPart = when (place.openNow) {
            true -> "open"
            false -> "closed"
            null -> "nearby"
        }
        return BestPickParts(
            openKey = openPart,
            rating = place.rating,
            walkMinutes = place.walkMinutes,
        )
    }
}

data class BestPickParts(
    val openKey: String,
    val rating: Double?,
    val walkMinutes: Int?,
)
