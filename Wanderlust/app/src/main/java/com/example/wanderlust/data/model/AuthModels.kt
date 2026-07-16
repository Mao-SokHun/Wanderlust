package com.example.wanderlust.data.model

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "USER",
    val companyName: String = "",
    val businessSubtype: String = "TOURS",
)

data class AuthResponse(
    val token: String,
    val id: String? = null,
    val name: String,
    val email: String,
    val role: String,
    val companyName: String? = null,
    val businessSubtype: String? = null,
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

data class MessageResponse(val message: String, val telegramUsername: String? = null)

data class SupportContactRequest(
    val topic: String = "App problem",
    val message: String,
    val replyEmail: String = "",
    val replyPhone: String = "",
)

data class AppSupportInfo(
    val telegramUsername: String = "",
    val telegramUrl: String = "",
    val email: String = "",
    val phone: String = "",
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val bio: String = "",
    val phone: String = "",
    val city: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val nationality: String = "Cambodia",
    val travelStyle: String = "",
    val emergencyContact: String = "",
    val language: String = "km",
    val themeDark: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
    val message: String? = null,
)

data class ProfileUpdateRequest(
    val name: String,
    val bio: String = "",
    val phone: String = "",
    val city: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val nationality: String = "Cambodia",
    val travelStyle: String = "",
    val emergencyContact: String = "",
    val language: String = "km",
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
