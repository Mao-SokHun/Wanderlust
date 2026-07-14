package com.example.wanderlust.navigation

import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.ui.components.WanderlustNavTab

/**
 * App navigation routes — one [AppScreen] = one full-screen in [com.example.wanderlust.MainActivity].
 */
sealed class AppScreen {
    data object Splash : AppScreen()
    data object Welcome : AppScreen()
    data object Login : AppScreen()
    data object Register : AppScreen()
    data object ForgotPassword : AppScreen()
    data class ResetPassword(val email: String = "", val token: String = "") : AppScreen()
    data object ChangePassword : AppScreen()

    /** Bottom tabs: Home / Explore / Saved / Profile */
    data class Main(val tab: WanderlustNavTab = WanderlustNavTab.Home) : AppScreen()

    data class TourDetail(val destination: DestinationCard) : AppScreen()
    data class AllDestinations(val category: String? = null) : AppScreen()
    data object MyTrips : AppScreen()
    data object Admin : AppScreen()
    data object AddTour : AppScreen()
    data object EditTour : AppScreen()
    data object ManageUsers : AppScreen()
    data object Analytics : AppScreen()
    data object EditProfile : AppScreen()
    data object Settings : AppScreen()
    data object HelpCenter : AppScreen()
    data class LegalDocument(val type: com.example.wanderlust.LegalDocumentType) : AppScreen()
    data object About : AppScreen()
    data object ExportData : AppScreen()
    data object AddSavedPlace : AppScreen()
}
