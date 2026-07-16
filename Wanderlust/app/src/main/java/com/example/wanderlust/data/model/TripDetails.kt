package com.example.wanderlust.data.model

/** Fixed-route bus / VIP trip details (stored in tours.package_details when listingType=TRIP). */
data class TripDetails(
    val kind: String = "trip",
    val departureCity: String = "",
    val arrivalCity: String = "",
    val travelDate: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val vehicleType: String = "",
    val amenities: List<String> = emptyList(),
    val totalSeats: Int = 15,
    val seatLayout: String = "ford_15",
    val boardingPoint: String = "",
    val boardingMapsUrl: String = "",
    val dropOffPoint: String = "",
    val dropOffNote: String = "station",
    val luggageKg: Int = 20,
    val childPolicy: String = "",
    val cancelHoursBefore: Int = 24,
    val cancelFeePercent: Int = 0,
    val rescheduleHoursBefore: Int = 12,
    val recurring: TripRecurring = TripRecurring(),
    val active: Boolean = true,
    val contact: TourContactInfo = TourContactInfo(),
    val imageUrls: List<String> = emptyList(),
)

data class TripRecurring(
    val enabled: Boolean = false,
    val pattern: String = "daily",
    val untilDate: String = "",
)

fun TripDetails.routeLabel(): String =
    if (departureCity.isNotBlank() && arrivalCity.isNotBlank()) {
        "$departureCity → $arrivalCity"
    } else {
        ""
    }

fun TripDetails.scheduleLabel(): String = buildString {
    if (travelDate.isNotBlank()) append(travelDate)
    if (departureTime.isNotBlank()) {
        if (isNotEmpty()) append(" · ")
        append(departureTime)
        if (arrivalTime.isNotBlank()) append(" → $arrivalTime")
    }
}
