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
    val packageDetails: TourPackageDetails? = null,
    val tripDetails: TripDetails? = null,
    val rentalDetails: RentalDetails? = null,
)

data class BillingPlan(
    val id: String,
    val name: String,
    val nameKh: String = "",
    val months: Int = 1,
    val priceUsd: Double = 0.0,
    val priceKhr: Int = 0,
    val priceKhrLabel: String = "",
    val description: String = "",
    val descriptionKh: String = "",
    val benefits: List<String> = emptyList(),
    val benefitsKh: List<String> = emptyList(),
    val active: Boolean = true,
)

data class BillingPlansResponse(
    val plans: List<BillingPlan> = emptyList(),
    val currency: String = "USD",
    val currencies: List<String> = listOf("USD", "KHR"),
    val usdToKhrRate: Double = 4100.0,
    val paymentProviders: PaymentProviders? = null,
)

data class PaymentProviders(
    val sandbox: PaymentProviderInfo? = null,
    val bakong: PaymentProviderInfo? = null,
)

data class PaymentProviderInfo(
    val enabled: Boolean = false,
    val label: String = "",
    val labelKh: String = "",
    val message: String = "",
    val messageKh: String = "",
    val method: String = "",
    val merchantName: String = "",
    val qrTtlMinutes: Int = 15,
    val currencies: List<String> = listOf("USD", "KHR"),
    val usdToKhrRate: Double = 4100.0,
    /** True when BAKONG_API_TOKEN / developer email is set on server. */
    val autoVerify: Boolean = false,
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
    val billNumber: String? = null,
    val md5: String? = null,
    val expiresAt: String? = null,
    val createdAt: String? = null,
)

data class BakongCreatePaymentRequest(
    val planId: String,
    val currency: String = "USD",
)

data class BakongCreatePaymentResponse(
    val configured: Boolean = true,
    val provider: String = "bakong",
    val autoVerify: Boolean = false,
    val payment: SandboxPayment? = null,
    val khqr: BakongKhqrPayload? = null,
    val deepLinks: List<BankDeepLink> = emptyList(),
    val message: String? = null,
    val messageKh: String? = null,
)

data class BakongKhqrPayload(
    val qr: String = "",
    val md5: String = "",
    val amountUsd: Double = 0.0,
    val amountKhr: Int = 0,
    val amount: Double = 0.0,
    val currency: String = "USD",
    val merchantName: String = "",
    val expiresAt: String? = null,
)

data class BankDeepLink(
    val id: String = "",
    val label: String = "",
    val labelKh: String = "",
    val scheme: String? = null,
    val androidPackage: String? = null,
    val playStoreUrl: String? = null,
    val hint: String? = null,
    val hintKh: String? = null,
)

data class BakongConfirmRequest(val paymentId: String)

data class BakongConfirmResponse(
    val pendingAdmin: Boolean = false,
    val paid: Boolean = false,
    val alreadyPaid: Boolean = false,
    val payment: SandboxPayment? = null,
    val subscription: SubscriptionStatus? = null,
    val message: String? = null,
    val messageKh: String? = null,
)

data class BakongPaymentStatusResponse(
    val payment: SandboxPayment? = null,
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

data class BusinessProfileUpdateRequest(
    val companyName: String? = null,
    val businessSubtype: String? = null,
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
    val packageDetails: TourPackageDetails? = null,
    val tripDetails: TripDetails? = null,
    val rentalDetails: RentalDetails? = null,
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
