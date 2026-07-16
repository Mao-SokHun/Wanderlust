package com.example.wanderlust.data.model

data class RentalDetails(
    val kind: String = "rental",
    val makeModel: String = "",
    val vehicleType: String = "",
    val year: String = "",
    val color: String = "",
    val condition: String = "",
    val seats: Int? = null,
    val withDriver: Boolean = true,
    val selfDrive: Boolean = false,
    val pricePerDay: Double? = null,
    val destinationRates: List<RentalDestinationRate> = emptyList(),
    val priceExclusions: List<String> = emptyList(),
    val requiredDocuments: List<String> = emptyList(),
    val securityDepositUsd: Double? = null,
    val fuelPolicy: String = "Full to Full",
    val lateReturnFeeUsd: Double? = null,
    val features: List<String> = emptyList(),
    val addOns: List<String> = emptyList(),
    val available: Boolean = true,
    val driver: RentalDriverProfile = RentalDriverProfile(),
    val contact: TourContactInfo = TourContactInfo(),
    val imageUrls: List<String> = emptyList(),
)

data class RentalDestinationRate(
    val from: String = "",
    val to: String = "",
    val priceUsd: Double = 0.0,
)

data class RentalDriverProfile(
    val name: String = "",
    val photoUrl: String = "",
    val languages: List<String> = emptyList(),
)
