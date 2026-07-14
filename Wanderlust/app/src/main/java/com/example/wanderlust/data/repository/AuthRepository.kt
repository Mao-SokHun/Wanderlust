package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.AuthResponse
import com.example.wanderlust.data.model.ChangePasswordRequest
import com.example.wanderlust.data.model.ForgotPasswordRequest
import com.example.wanderlust.data.model.LoginRequest
import com.example.wanderlust.data.model.MessageResponse
import com.example.wanderlust.data.model.ProfileUpdateRequest
import com.example.wanderlust.data.model.RegisterRequest
import com.example.wanderlust.data.model.ResetPasswordRequest
import com.example.wanderlust.data.model.UserProfile

class AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        apiCall { api ->
            val response = api.login(LoginRequest(email.trim().lowercase(), password))
            SessionManager.saveLogin(
                token = response.token,
                name = response.name,
                role = response.role,
                userId = response.id,
                email = response.email,
            )
            syncProfileFromServer(api)
            response
        }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> =
        apiCall { api ->
            val response = api.register(RegisterRequest(name, email.trim().lowercase(), password))
            SessionManager.saveLogin(
                token = response.token,
                name = response.name,
                role = response.role,
                userId = response.id,
                email = response.email,
            )
            syncProfileFromServer(api)
            response
        }

    suspend fun fetchProfile(): Result<UserProfile> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { api ->
            val profile = api.getProfile(header)
            SessionManager.applyProfile(profile)
            profile
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<UserProfile> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { api ->
            val response = api.updateProfile(header, request)
            SessionManager.applyProfile(response)
            response
        }
    }

    suspend fun forgotPassword(email: String) =
        apiCall { it.forgotPassword(ForgotPasswordRequest(email.trim().lowercase())) }

    suspend fun resetPassword(email: String, token: String, newPassword: String): Result<MessageResponse> =
        apiCall {
            it.resetPassword(
                ResetPasswordRequest(
                    email = email.trim().lowercase(),
                    token = token.trim().uppercase(),
                    newPassword = newPassword,
                ),
            )
        }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<MessageResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { api ->
            api.changePassword(header, ChangePasswordRequest(currentPassword, newPassword))
        }
    }

    private suspend fun syncProfileFromServer(api: com.example.wanderlust.data.remote.WanderlustApi) {
        val header = SessionManager.authHeader() ?: return
        runCatching { api.getProfile(header) }
            .onSuccess { SessionManager.applyProfile(it) }
    }
}
