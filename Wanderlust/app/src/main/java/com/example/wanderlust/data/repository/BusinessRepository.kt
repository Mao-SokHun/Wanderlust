package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.BillingPlansResponse
import com.example.wanderlust.data.model.BusinessProfile
import com.example.wanderlust.data.model.BusinessProfileUpdateRequest
import com.example.wanderlust.data.model.BusinessTourRequest
import com.example.wanderlust.data.model.CancelSubscriptionResponse
import com.example.wanderlust.data.model.BakongConfirmRequest
import com.example.wanderlust.data.model.BakongConfirmResponse
import com.example.wanderlust.data.model.BakongCreatePaymentRequest
import com.example.wanderlust.data.model.BakongCreatePaymentResponse
import com.example.wanderlust.data.model.SandboxPayRequest
import com.example.wanderlust.data.model.SandboxPayResponse
import com.example.wanderlust.data.model.SubscriptionStatus
import com.example.wanderlust.data.model.Tour

class BusinessRepository {

    suspend fun getPlans(): Result<BillingPlansResponse> =
        apiCall { it.getBillingPlans() }

    suspend fun getSubscription(): Result<SubscriptionStatus> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.getSubscription(header) }
    }

    suspend fun sandboxPay(planId: String): Result<SandboxPayResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.sandboxPay(header, SandboxPayRequest(planId)) }
    }

    suspend fun createBakongPayment(planId: String, currency: String = "USD"): Result<BakongCreatePaymentResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall {
            it.createBakongPayment(header, BakongCreatePaymentRequest(planId, currency))
        }
    }

    suspend fun confirmBakongPayment(paymentId: String): Result<BakongConfirmResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.confirmBakongPayment(header, BakongConfirmRequest(paymentId)) }
    }

    suspend fun cancelSubscription(): Result<CancelSubscriptionResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.cancelSubscription(header) }
    }

    suspend fun getBusinessProfile(): Result<BusinessProfile> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.getBusinessProfile(header) }
    }

    suspend fun updateBusinessProfile(request: BusinessProfileUpdateRequest): Result<BusinessProfile> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.updateBusinessProfile(header, request) }
    }

    suspend fun getMyTours(): Result<List<Tour>> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.getBusinessTours(header) }
    }

    suspend fun createTour(request: BusinessTourRequest): Result<Tour> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.createBusinessTour(header, request) }
    }

    suspend fun updateTour(id: String, request: BusinessTourRequest): Result<Tour> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.updateBusinessTour(header, id, request) }
    }

    suspend fun deleteTour(id: String): Result<Unit> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall {
            it.deleteBusinessTour(header, id)
            Unit
        }
    }
}
