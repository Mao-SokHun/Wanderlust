package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.AdminAnalytics
import com.example.wanderlust.data.model.AdminBillingPlanUpdateRequest
import com.example.wanderlust.data.model.AdminBillingPlanUpdateResponse
import com.example.wanderlust.data.model.AdminBillingPlansResponse
import com.example.wanderlust.data.model.AdminBillingSettingsRequest
import com.example.wanderlust.data.model.AdminBillingSettingsResponse
import com.example.wanderlust.data.model.AdminPaymentActionResponse
import com.example.wanderlust.data.model.AdminPendingPaymentsResponse
import com.example.wanderlust.data.model.AdminStats
import com.example.wanderlust.data.model.AdminTourRequest
import com.example.wanderlust.data.model.AdminUser
import com.example.wanderlust.data.model.Tour

class AdminRepository {

    private fun bearerToken(): String? = SessionManager.token?.let { "Bearer $it" }

    suspend fun getStats(): Result<AdminStats> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAdminStats(token) }
    }

    suspend fun getTours(): Result<List<Tour>> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAdminTours(token) }
    }

    suspend fun addTour(request: AdminTourRequest): Result<Tour> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.addTour(token, request) }
    }

    suspend fun updateTour(id: String, request: AdminTourRequest): Result<Tour> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.updateTour(token, id, request) }
    }

    suspend fun getUsers(): Result<List<AdminUser>> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAdminUsers(token) }
    }

    suspend fun getAnalytics(): Result<AdminAnalytics> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAnalytics(token) }
    }

    suspend fun getBillingPlans(): Result<AdminBillingPlansResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAdminBillingPlans(token) }
    }

    suspend fun updateBillingPlan(
        id: String,
        request: AdminBillingPlanUpdateRequest,
    ): Result<AdminBillingPlanUpdateResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.updateAdminBillingPlan(token, id, request) }
    }

    suspend fun updateBillingSettings(
        request: AdminBillingSettingsRequest,
    ): Result<AdminBillingSettingsResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.updateAdminBillingSettings(token, request) }
    }

    suspend fun getPendingPayments(status: String = "pending"): Result<AdminPendingPaymentsResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.getAdminBillingPayments(token, status) }
    }

    suspend fun approvePayment(id: String): Result<AdminPaymentActionResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.approveAdminBillingPayment(token, id) }
    }

    suspend fun rejectPayment(id: String): Result<AdminPaymentActionResponse> {
        val token = bearerToken()
            ?: return Result.failure(Exception("Please log in as admin first"))
        return apiCall { it.rejectAdminBillingPayment(token, id) }
    }
}
