package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.AdminAnalytics
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
}
