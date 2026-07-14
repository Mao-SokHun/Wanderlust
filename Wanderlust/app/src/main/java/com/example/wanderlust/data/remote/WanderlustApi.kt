package com.example.wanderlust.data.remote

import com.example.wanderlust.data.model.AdminStats
import com.example.wanderlust.data.model.AdminTourRequest
import com.example.wanderlust.data.model.AdminUser
import com.example.wanderlust.data.model.AdminAnalytics
import com.example.wanderlust.data.model.AppVersionInfo
import com.example.wanderlust.data.model.AuthResponse
import com.example.wanderlust.data.model.HealthResponse
import com.example.wanderlust.data.model.FavoriteRequest
import com.example.wanderlust.data.model.LoginRequest
import com.example.wanderlust.data.model.ChangePasswordRequest
import com.example.wanderlust.data.model.ForgotPasswordRequest
import com.example.wanderlust.data.model.ForgotPasswordResponse
import com.example.wanderlust.data.model.MessageResponse
import com.example.wanderlust.data.model.UserProfile
import com.example.wanderlust.data.model.ProfileUpdateRequest
import com.example.wanderlust.data.model.RegisterRequest
import com.example.wanderlust.data.model.ResetPasswordRequest
import com.example.wanderlust.data.model.Tour
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WanderlustApi {

    @GET("/")
    suspend fun health(): HealthResponse

    @GET("api/app/version")
    suspend fun getAppVersion(): AppVersionInfo

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest,
    ): MessageResponse

    @GET("api/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): UserProfile

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest,
    ): UserProfile

    @GET("api/tours")
    suspend fun getTours(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
    ): List<Tour>

    @GET("api/favorites")
    suspend fun getFavorites(@Header("Authorization") token: String): List<Tour>

    @POST("api/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: FavoriteRequest,
    )

    @DELETE("api/favorites/{tourId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("tourId") tourId: String,
        @retrofit2.http.Query("title") title: String? = null,
    )

    @GET("api/admin/stats")
    suspend fun getAdminStats(@Header("Authorization") token: String): AdminStats

    @GET("api/admin/tours")
    suspend fun getAdminTours(@Header("Authorization") token: String): List<Tour>

    @POST("api/admin/tours")
    suspend fun addTour(
        @Header("Authorization") token: String,
        @Body request: AdminTourRequest,
    ): Tour

    @PUT("api/admin/tours/{id}")
    suspend fun updateTour(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: AdminTourRequest,
    ): Tour

    @GET("api/admin/users")
    suspend fun getAdminUsers(@Header("Authorization") token: String): List<AdminUser>

    @GET("api/admin/analytics")
    suspend fun getAnalytics(@Header("Authorization") token: String): AdminAnalytics
}
