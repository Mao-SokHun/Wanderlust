package com.example.wanderlust.data.model

data class AdminTourRequest(
    val title: String,
    val description: String,
    val category: String,
    val rating: Double,
)

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
)

data class AdminAnalytics(
    val tours: Int,
    val users: Int,
    val averageRating: Double,
    val topCategory: String,
)

data class AdminBillingPlansResponse(
    val plans: List<BillingPlan> = emptyList(),
    val usdToKhrRate: Double = 4100.0,
    val currencies: List<String> = listOf("USD", "KHR"),
)

data class AdminBillingPlanUpdateRequest(
    val priceUsd: Double? = null,
    val name: String? = null,
    val nameKh: String? = null,
    val description: String? = null,
    val descriptionKh: String? = null,
    val months: Int? = null,
    val active: Boolean? = null,
)

data class AdminBillingPlanUpdateResponse(
    val plan: BillingPlan? = null,
    val usdToKhrRate: Double = 4100.0,
)

data class AdminBillingSettingsRequest(
    val usdToKhrRate: Double? = null,
)

data class AdminBillingSettingsResponse(
    val usdToKhrRate: Double = 4100.0,
)

data class AdminPendingPayment(
    val id: String,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val companyName: String = "",
    val planId: String = "",
    val amountUsd: Double = 0.0,
    val currency: String = "USD",
    val status: String = "pending",
    val billNumber: String = "",
    val md5: String = "",
    val createdAt: String? = null,
    val reportedAt: String? = null,
)

data class AdminPendingPaymentsResponse(
    val payments: List<AdminPendingPayment> = emptyList(),
)

data class AdminPaymentActionResponse(
    val payment: AdminPendingPayment? = null,
    val message: String? = null,
)
