package com.example.wanderlust.data.remote

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
import com.example.wanderlust.data.model.AppVersionInfo
import com.example.wanderlust.data.model.AuthResponse
import com.example.wanderlust.data.model.BillingPlansResponse
import com.example.wanderlust.data.model.BusinessProfile
import com.example.wanderlust.data.model.BusinessProfileUpdateRequest
import com.example.wanderlust.data.model.BusinessTourRequest
import com.example.wanderlust.data.model.CancelSubscriptionResponse
import com.example.wanderlust.data.model.HealthResponse
import com.example.wanderlust.data.model.FavoriteRequest
import com.example.wanderlust.data.model.LoginRequest
import com.example.wanderlust.data.model.ChangePasswordRequest
import com.example.wanderlust.data.model.ForgotPasswordRequest
import com.example.wanderlust.data.model.ForgotPasswordResponse
import com.example.wanderlust.data.model.MessageResponse
import com.example.wanderlust.data.model.UserProfile
import com.example.wanderlust.data.model.ProfileUpdateRequest
import com.example.wanderlust.data.model.RateTourRequest
import com.example.wanderlust.data.model.RateTourResponse
import com.example.wanderlust.data.model.MyTourRatingResponse
import com.example.wanderlust.data.model.RegisterRequest
import com.example.wanderlust.data.model.ResetPasswordRequest
import com.example.wanderlust.data.model.SandboxPayRequest
import com.example.wanderlust.data.model.SandboxPayResponse
import com.example.wanderlust.data.model.BakongCreatePaymentRequest
import com.example.wanderlust.data.model.BakongCreatePaymentResponse
import com.example.wanderlust.data.model.BakongConfirmRequest
import com.example.wanderlust.data.model.BakongConfirmResponse
import com.example.wanderlust.data.model.BakongPaymentStatusResponse
import com.example.wanderlust.data.model.ImageUploadResponse
import com.example.wanderlust.data.model.SubscriptionStatus
import com.example.wanderlust.data.model.SupportContactRequest
import com.example.wanderlust.data.model.AppSupportInfo
import com.example.wanderlust.data.model.Tour
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface WanderlustApi {

    @GET("/")
    suspend fun health(): HealthResponse

    @GET("api/app/version")
    suspend fun getAppVersion(): AppVersionInfo

    @GET("api/app/support")
    suspend fun getAppSupport(): AppSupportInfo

    @POST("api/support/contact")
    suspend fun sendSupportContact(
        @Header("Authorization") token: String? = null,
        @Body request: SupportContactRequest,
    ): MessageResponse

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
        @Query("listingType") listingType: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("top") top: Int? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("radiusKm") radiusKm: Double? = null,
    ): List<Tour>

    @POST("api/tours/{id}/ratings")
    suspend fun rateTour(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: RateTourRequest,
    ): RateTourResponse

    @GET("api/tours/{id}/ratings/me")
    suspend fun getMyTourRating(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): MyTourRatingResponse

    @GET("api/billing/plans")
    suspend fun getBillingPlans(): BillingPlansResponse

    @GET("api/billing/subscription")
    suspend fun getSubscription(@Header("Authorization") token: String): SubscriptionStatus

    @POST("api/billing/sandbox/pay")
    suspend fun sandboxPay(
        @Header("Authorization") token: String,
        @Body request: SandboxPayRequest,
    ): SandboxPayResponse

    @POST("api/billing/bakong/create-payment")
    suspend fun createBakongPayment(
        @Header("Authorization") token: String,
        @Body request: BakongCreatePaymentRequest,
    ): BakongCreatePaymentResponse

    @GET("api/billing/bakong/payments/{id}")
    suspend fun getBakongPayment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): BakongPaymentStatusResponse

    @POST("api/billing/bakong/confirm")
    suspend fun confirmBakongPayment(
        @Header("Authorization") token: String,
        @Body request: BakongConfirmRequest,
    ): BakongConfirmResponse

    @POST("api/billing/subscription/cancel")
    suspend fun cancelSubscription(
        @Header("Authorization") token: String,
    ): CancelSubscriptionResponse

    @GET("api/business/me")
    suspend fun getBusinessProfile(@Header("Authorization") token: String): BusinessProfile

    @PUT("api/business/me")
    suspend fun updateBusinessProfile(
        @Header("Authorization") token: String,
        @Body request: BusinessProfileUpdateRequest,
    ): BusinessProfile

    @GET("api/business/tours")
    suspend fun getBusinessTours(@Header("Authorization") token: String): List<Tour>

    @POST("api/business/tours")
    suspend fun createBusinessTour(
        @Header("Authorization") token: String,
        @Body request: BusinessTourRequest,
    ): Tour

    @Multipart
    @POST("api/business/upload-images")
    suspend fun uploadBusinessImages(
        @Header("Authorization") token: String,
        @Part images: List<MultipartBody.Part>,
    ): ImageUploadResponse

    @PUT("api/business/tours/{id}")
    suspend fun updateBusinessTour(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: BusinessTourRequest,
    ): Tour

    @DELETE("api/business/tours/{id}")
    suspend fun deleteBusinessTour(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): MessageResponse

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

    @GET("api/admin/billing/plans")
    suspend fun getAdminBillingPlans(@Header("Authorization") token: String): AdminBillingPlansResponse

    @PUT("api/admin/billing/plans/{id}")
    suspend fun updateAdminBillingPlan(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: AdminBillingPlanUpdateRequest,
    ): AdminBillingPlanUpdateResponse

    @PUT("api/admin/billing/settings")
    suspend fun updateAdminBillingSettings(
        @Header("Authorization") token: String,
        @Body request: AdminBillingSettingsRequest,
    ): AdminBillingSettingsResponse

    @GET("api/admin/billing/payments")
    suspend fun getAdminBillingPayments(
        @Header("Authorization") token: String,
        @Query("status") status: String = "pending",
    ): AdminPendingPaymentsResponse

    @POST("api/admin/billing/payments/{id}/approve")
    suspend fun approveAdminBillingPayment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): AdminPaymentActionResponse

    @POST("api/admin/billing/payments/{id}/reject")
    suspend fun rejectAdminBillingPayment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): AdminPaymentActionResponse
}
