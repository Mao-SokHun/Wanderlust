package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.BillingPlan
import com.example.wanderlust.data.model.BusinessProfile
import com.example.wanderlust.data.model.BusinessTourRequest
import com.example.wanderlust.data.model.CancelSubscriptionResponse
import com.example.wanderlust.data.model.SandboxPayRequest
import com.example.wanderlust.data.model.SandboxPayResponse
import com.example.wanderlust.data.model.SubscriptionStatus
import com.example.wanderlust.data.model.Tour

class BusinessRepository {

    suspend fun getPlans(): Result<List<BillingPlan>> =
        apiCall { it.getBillingPlans().plans }

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
