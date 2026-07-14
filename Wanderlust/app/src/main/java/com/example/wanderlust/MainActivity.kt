package com.example.wanderlust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.repository.AppUpdateAvailability
import com.example.wanderlust.data.repository.AppUpdateRepository
import com.example.wanderlust.navigation.AppNavigator
import com.example.wanderlust.navigation.AppScreen
import com.example.wanderlust.ui.components.AppUpdateDialog
import com.example.wanderlust.ui.components.WanderlustNavTab
import com.example.wanderlust.ui.theme.WanderlustTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val nav = remember { AppNavigator() }
            var mainTab by remember { mutableStateOf(WanderlustNavTab.Home) }
            var exploreQuery by remember { mutableStateOf("") }
            var exploreCategory by remember { mutableStateOf<String?>(null) }
            var savedRefreshKey by remember { mutableIntStateOf(0) }
            var pendingUpdate by remember { mutableStateOf<AppUpdateAvailability?>(null) }

            LaunchedEffect(Unit) {
                isDarkTheme = SessionManager.userThemeDark
                AppUpdateRepository().checkForUpdate()
                    .onSuccess { update -> pendingUpdate = update }
            }

            fun applySessionPreferences() {
                if (SessionManager.isLoggedIn()) {
                    isDarkTheme = SessionManager.userThemeDark
                }
            }

            fun syncTabFromStack() {
                mainTab = nav.mainTabOrDefault(mainTab)
            }

            val canGoBack = nav.stack.size > 1
            BackHandler(enabled = canGoBack) {
                if (nav.pop()) {
                    syncTabFromStack()
                }
            }

            WanderlustTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Box(Modifier.fillMaxSize().statusBarsPadding()) {
                            val toggleTheme = {
                                val newDark = !isDarkTheme
                                isDarkTheme = newDark
                                if (SessionManager.isLoggedIn()) {
                                    SessionManager.setThemeDark(newDark)
                                }
                            }
                            val openLogin: () -> Unit = { nav.push(AppScreen.Login) }
                            val openRegister: () -> Unit = { nav.push(AppScreen.Register) }
                            val requireLogin: (() -> Unit) -> Unit = { action ->
                                if (SessionManager.isLoggedIn()) action() else openLogin()
                            }
                            val openDestination: (com.example.wanderlust.data.DestinationCard) -> Unit = { dest ->
                                if (GuestAccess.canViewDestination(dest)) {
                                    nav.push(AppScreen.TourDetail(dest))
                                } else {
                                    openLogin()
                                }
                            }

                            when (val current = nav.current) {
                                AppScreen.Splash -> SplashScreen(
                                    onFinished = {
                                        if (SessionManager.isLoggedIn()) {
                                            mainTab = WanderlustNavTab.Home
                                            nav.resetTo(AppScreen.Main(WanderlustNavTab.Home))
                                        } else {
                                            nav.resetTo(AppScreen.Welcome)
                                        }
                                    },
                                )

                                AppScreen.Welcome -> WelcomeScreen(
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = toggleTheme,
                                    onGetStarted = {
                                        SessionManager.clear()
                                        mainTab = WanderlustNavTab.Home
                                        nav.resetTo(AppScreen.Main(WanderlustNavTab.Home))
                                    },
                                    onLogin = { nav.push(AppScreen.Login) },
                                    onRegister = { nav.push(AppScreen.Register) },
                                    onGoogleContinue = { nav.push(AppScreen.Login) },
                                    onAppleContinue = { nav.push(AppScreen.Register) },
                                )

                                AppScreen.Login -> LoginScreen(
                                    onLoginSuccess = {
                                        applySessionPreferences()
                                        mainTab = WanderlustNavTab.Home
                                        nav.resetTo(AppScreen.Main(WanderlustNavTab.Home))
                                    },
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onSignUp = { nav.push(AppScreen.Register) },
                                    onForgotPassword = { nav.push(AppScreen.ForgotPassword) },
                                )

                                AppScreen.ForgotPassword -> ForgotPasswordScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onResetPassword = { email, token ->
                                        nav.push(AppScreen.ResetPassword(email, token))
                                    },
                                )

                                is AppScreen.ResetPassword -> ResetPasswordScreen(
                                    initialEmail = current.email,
                                    initialToken = current.token,
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onSuccess = { nav.resetTo(AppScreen.Login) },
                                )

                                AppScreen.ChangePassword -> ChangePasswordScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.Register -> RegisterScreen(
                                    onRegisterSuccess = {
                                        applySessionPreferences()
                                        mainTab = WanderlustNavTab.Home
                                        nav.resetTo(AppScreen.Main(WanderlustNavTab.Home))
                                    },
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onSignIn = {
                                        if (nav.current == AppScreen.Register) {
                                            nav.pop()
                                        }
                                        nav.push(AppScreen.Login)
                                    },
                                )

                                is AppScreen.Main -> MainShellScreen(
                                    selectedTab = current.tab,
                                    onTabChange = { tab ->
                                        mainTab = tab
                                        nav.switchMainTab(tab)
                                    },
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = toggleTheme,
                                    onDestinationClick = openDestination,
                                    onSignIn = openLogin,
                                    onRegister = openRegister,
                                    exploreInitialQuery = exploreQuery,
                                    exploreInitialCategory = exploreCategory,
                                    savedRefreshKey = savedRefreshKey,
                                    onSearch = { query ->
                                        exploreQuery = query
                                        mainTab = WanderlustNavTab.Explore
                                        nav.switchMainTab(WanderlustNavTab.Explore)
                                    },
                                    onViewAllDestinations = {
                                        nav.push(AppScreen.AllDestinations(exploreCategory))
                                    },
                                    onCategoryExplore = { cat ->
                                        exploreCategory = cat
                                        mainTab = WanderlustNavTab.Explore
                                        nav.switchMainTab(WanderlustNavTab.Explore)
                                    },
                                    onOpenSavedPlans = { requireLogin { nav.push(AppScreen.MyTrips) } },
                                    onOpenSettings = { nav.push(AppScreen.Settings) },
                                    onOpenHelp = { nav.push(AppScreen.HelpCenter) },
                                    onOpenPrivacy = { nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.PrivacyPolicy)) },
                                    onOpenTerms = { nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.TermsOfService)) },
                                    onOpenAbout = { nav.push(AppScreen.About) },
                                    onLogout = {
                                        SessionManager.clear()
                                        nav.resetTo(AppScreen.Welcome)
                                    },
                                    onAddSavedPlace = {
                                        requireLogin { nav.push(AppScreen.AddSavedPlace) }
                                    },
                                )

                                is AppScreen.AllDestinations -> AllDestinationsScreen(
                                    initialCategory = current.category,
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onDestinationClick = openDestination,
                                    onSignIn = openLogin,
                                )

                                is AppScreen.TourDetail -> TourDetailScreen(
                                    destination = current.destination,
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onSavePlace = {
                                        savedRefreshKey++
                                        mainTab = WanderlustNavTab.Saved
                                        nav.popToMain(WanderlustNavTab.Saved)
                                    },
                                    onOpenNearby = openDestination,
                                    onSignIn = openLogin,
                                    onRegister = openRegister,
                                )

                                AppScreen.MyTrips -> MyBookingsScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onOpenSaved = {
                                        mainTab = WanderlustNavTab.Saved
                                        nav.popToMain(WanderlustNavTab.Saved)
                                    },
                                )

                                AppScreen.Admin -> AdminScreen(
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = toggleTheme,
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onOpenBookings = { nav.push(AppScreen.MyTrips) },
                                    onExportData = { nav.push(AppScreen.ExportData) },
                                    onAddTour = { nav.push(AppScreen.AddTour) },
                                    onEditTour = { nav.push(AppScreen.EditTour) },
                                    onManageUsers = { nav.push(AppScreen.ManageUsers) },
                                    onOpenAnalytics = { nav.push(AppScreen.Analytics) },
                                )

                                AppScreen.AddTour -> AddTourScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.EditTour -> EditTourScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.ManageUsers -> ManageUsersScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.Analytics -> AnalyticsScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.EditProfile -> EditProfileScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onChangePassword = {
                                        nav.push(AppScreen.ChangePassword)
                                    },
                                )

                                AppScreen.Settings -> SettingsScreen(
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = toggleTheme,
                                    onOpenPrivacy = {
                                        nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.PrivacyPolicy))
                                    },
                                    onOpenTerms = {
                                        nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.TermsOfService))
                                    },
                                    onOpenAbout = { nav.push(AppScreen.About) },
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                is AppScreen.LegalDocument -> LegalDocumentScreen(
                                    type = current.type,
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.About -> AboutScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.HelpCenter -> HelpCenterScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onGoHome = {
                                        mainTab = WanderlustNavTab.Home
                                        nav.popToMain(WanderlustNavTab.Home)
                                    },
                                )

                                AppScreen.ExportData -> ExportDataScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                )

                                AppScreen.AddSavedPlace -> AddSavedPlaceScreen(
                                    onBack = {
                                        nav.pop()
                                        syncTabFromStack()
                                    },
                                    onSaved = { dest ->
                                        savedRefreshKey++
                                        nav.pop()
                                        nav.push(AppScreen.TourDetail(dest))
                                    },
                                )
                            }
                            pendingUpdate?.let { update ->
                                AppUpdateDialog(
                                    update = update,
                                    onDismiss = { pendingUpdate = null },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
