package com.example.wanderlust.data.model

data class Tour(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val rating: Double,
    val ratingCount: Int = 0,
    val location: String = "",
    val priceLabel: String = "",
    val priceUsd: Double? = null,
    val duration: String = "",
    val imageUrl: String = "",
    val ownerId: String? = null,
    val businessName: String? = null,
    val status: String = "published",
    val listingType: String = "TOUR",
    val vehicleType: String = "",
    val seats: Int? = null,
    val transmission: String = "",
    val fuelType: String = "",
    val rateUnit: String = "day",
    val serviceArea: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceKm: Double? = null,
)

data class BillingPlan(
    val id: String,
    val name: String,
    val nameKh: String = "",
    val months: Int = 1,
    val priceUsd: Double = 0.0,
    val description: String = "",
    val descriptionKh: String = "",
    val benefits: List<String> = emptyList(),
    val benefitsKh: List<String> = emptyList(),
)

data class BillingPlansResponse(
    val plans: List<BillingPlan> = emptyList(),
    val currency: String = "USD",
)

data class SubscriptionStatus(
    val active: Boolean = false,
    val status: String = "none",
    val planId: String? = null,
    val planName: String? = null,
    val planNameKh: String? = null,
    val startedAt: String? = null,
    val expiresAt: String? = null,
    val canPost: Boolean = false,
    val cancelAtPeriodEnd: Boolean = false,
    val canceledAt: String? = null,
    val refundable: Boolean = false,
    val benefits: List<String> = emptyList(),
    val benefitsKh: List<String> = emptyList(),
)

data class CancelSubscriptionResponse(
    val subscription: SubscriptionStatus? = null,
    val alreadyCanceled: Boolean = false,
    val refundable: Boolean = false,
    val message: String? = null,
    val messageKh: String? = null,
    val expiresAt: String? = null,
)

data class SandboxPayRequest(val planId: String)

data class SandboxPayResponse(
    val payment: SandboxPayment? = null,
    val subscription: SubscriptionStatus? = null,
    val message: String? = null,
)

data class SandboxPayment(
    val id: String = "",
    val planId: String = "",
    val amountUsd: Double = 0.0,
    val provider: String = "sandbox",
    val status: String = "",
    val paidAt: String? = null,
)

data class BusinessProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val companyName: String = "",
    val businessSubtype: String = "TOURS",
    val subscription: SubscriptionStatus = SubscriptionStatus(),
    val tourCount: Int = 0,
)

data class BusinessTourRequest(
    val title: String,
    val description: String = "",
    val category: String = "Tour",
    val location: String = "",
    val priceLabel: String = "",
    val priceUsd: Double? = null,
    val duration: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val listingType: String = "TOUR",
    val vehicleType: String = "",
    val seats: Int? = null,
    val transmission: String = "",
    val fuelType: String = "",
    val rateUnit: String = "day",
    val serviceArea: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String? = null,
)

data class RateTourRequest(
    val stars: Int,
    val comment: String = "",
)

data class RateTourResponse(
    val tourId: String = "",
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val myRating: Int? = null,
    val message: String? = null,
)

data class MyTourRatingResponse(
    val tourId: String = "",
    val myRating: Int? = null,
    val comment: String = "",
)
