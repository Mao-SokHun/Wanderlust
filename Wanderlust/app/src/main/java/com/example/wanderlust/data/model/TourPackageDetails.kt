package com.example.wanderlust.data.model

/** Rich Cambodia tour package details (stored as tours.package_details JSON). */
data class TourPackageDetails(
    val tourType: String = "",
    val days: Int = 1,
    val nights: Int = 0,
    val departureDate: String = "",
    val priceType: String = "per_person",
    val inclusions: TourInclusions = TourInclusions(),
    val exclusions: List<String> = emptyList(),
    val itinerary: List<TourItineraryDay> = emptyList(),
    val booking: TourBookingTerms = TourBookingTerms(),
    val contact: TourContactInfo = TourContactInfo(),
    val imageUrls: List<String> = emptyList(),
)

data class TourInclusions(
    val transport: List<String> = emptyList(),
    val accommodationType: String = "",
    val accommodationStars: String = "",
    val occupancy: String = "",
    val meals: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val insurance: Boolean = false,
    val custom: List<String> = emptyList(),
)

data class TourItineraryDay(
    val day: Int = 1,
    val title: String = "",
    val items: List<TourItineraryItem> = emptyList(),
)

data class TourItineraryItem(
    val time: String = "",
    val activity: String = "",
    val location: String = "",
)

data class TourBookingTerms(
    val depositPercent: Int = 50,
    val cancellationRules: List<TourCancelRule> = listOf(
        TourCancelRule(beforeDays = 7, refundPercent = 100),
        TourCancelRule(beforeDays = 3, refundPercent = 50),
        TourCancelRule(beforeDays = 0, refundPercent = 0),
    ),
    val whatToBring: List<String> = emptyList(),
)

data class TourCancelRule(
    val beforeDays: Int = 0,
    val refundPercent: Int = 0,
)

data class TourContactInfo(
    val phone: String = "",
    val telegram: String = "",
    val messenger: String = "",
)

fun TourPackageDetails.durationLabel(): String {
    val parts = buildList {
        if (days > 0) add(if (days == 1) "1 day" else "$days days")
        if (nights > 0) add(if (nights == 1) "1 night" else "$nights nights")
    }
    return parts.joinToString(" · ")
}
