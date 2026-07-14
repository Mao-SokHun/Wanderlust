package com.example.wanderlust.data.model

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

data class AuthResponse(
    val token: String,
    val id: String? = null,
    val name: String,
    val email: String,
    val role: String,
)

data class ForgotPasswordRequest(val email: String)

data class ForgotPasswordResponse(
    val message: String,
    val resetToken: String? = null,
    val email: String? = null,
)

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String,
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)

data class MessageResponse(val message: String)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val bio: String = "",
    val language: String = "en",
    val themeDark: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
    val message: String? = null,
)

data class ProfileUpdateRequest(
    val name: String,
    val bio: String = "",
    val language: String = "en",
    val themeDark: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
)

/** @deprecated Use [UserProfile] */
typealias ProfileResponse = UserProfile

data class AdminStats(
    val activeTours: Int,
    val users: Int,
)
