package com.example.wanderlust

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.repository.AppUpdateAvailability
import com.example.wanderlust.data.repository.AppUpdateRepository
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.navigation.AppNavigator
import com.example.wanderlust.navigation.AppScreen
import com.example.wanderlust.navigation.BackNavResult
import com.example.wanderlust.ui.components.AppUpdateDialog
import com.example.wanderlust.ui.components.WanderlustNavTab
import com.example.wanderlust.ui.theme.WanderlustTheme
import androidx.compose.runtime.key

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val nav = remember { AppNavigator() }
            var mainTab by remember { mutableStateOf(WanderlustNavTab.Home) }
            var savedRefreshKey by remember { mutableIntStateOf(0) }
            var pendingUpdate by remember { mutableStateOf<AppUpdateAvailability?>(null) }
            var lastExitPromptAtMs by remember { mutableLongStateOf(0L) }
            val context = LocalContext.current

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

            fun navigateBack() {
                nav.popOr(
                    when {
                        SessionManager.isLoggedIn() -> AppScreen.Main(WanderlustNavTab.Home)
                        nav.stack.any { it is AppScreen.Main } -> AppScreen.Main(WanderlustNavTab.Home)
                        else -> AppScreen.Welcome
                    },
                )
                syncTabFromStack()
            }

            BackHandler(enabled = true) {
                val confirmPending =
                    SystemClock.elapsedRealtime() - lastExitPromptAtMs < EXIT_CONFIRM_WINDOW_MS
                when (nav.handleSystemBack(confirmExitPending = confirmPending)) {
                    BackNavResult.Consumed -> {
                        lastExitPromptAtMs = 0L
                        syncTabFromStack()
                    }
                    BackNavResult.ConfirmExit -> {
                        lastExitPromptAtMs = SystemClock.elapsedRealtime()
                        Toast.makeText(
                            context,
                            context.getString(R.string.msg_press_back_again_exit),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    BackNavResult.ExitApp -> finish()
                }
            }

            val appLanguage = AppLocale.code
            key(appLanguage) {
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
                                    onBack = { navigateBack() },
                                    onSignUp = { nav.push(AppScreen.Register) },
                                    onForgotPassword = { nav.push(AppScreen.ForgotPassword) },
                                )

                                AppScreen.ForgotPassword -> ForgotPasswordScreen(
                                    onBack = { navigateBack() },
                                    onResetPassword = { email, token ->
                                        nav.push(AppScreen.ResetPassword(email, token))
                                    },
                                )

                                is AppScreen.ResetPassword -> ResetPasswordScreen(
                                    initialEmail = current.email,
                                    initialToken = current.token,
                                    onBack = { navigateBack() },
                                    onSuccess = { nav.resetTo(AppScreen.Login) },
                                )

                                AppScreen.ChangePassword -> ChangePasswordScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.Register -> RegisterScreen(
                                    onRegisterSuccess = {
                                        applySessionPreferences()
                                        mainTab = WanderlustNavTab.Home
                                        nav.resetTo(AppScreen.Main(WanderlustNavTab.Home))
                                    },
                                    onBack = { navigateBack() },
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
                                    savedRefreshKey = savedRefreshKey,
                                    onOpenSavedPlans = { requireLogin { nav.push(AppScreen.MyTrips) } },
                                    onOpenSettings = { nav.push(AppScreen.Settings) },
                                    onOpenHelp = { nav.push(AppScreen.HelpCenter) },
                                    onOpenPrivacy = { nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.PrivacyPolicy)) },
                                    onOpenTerms = { nav.push(AppScreen.LegalDocument(com.example.wanderlust.LegalDocumentType.TermsOfService)) },
                                    onOpenAbout = { nav.push(AppScreen.About) },
                                    onOpenBusinessStudio = {
                                        requireLogin { nav.push(AppScreen.BusinessStudio) }
                                    },
                                    onLogout = {
                                        SessionManager.clear()
                                        nav.resetTo(AppScreen.Welcome)
                                    },
                                    onAddSavedPlace = {
                                        requireLogin { nav.push(AppScreen.AddSavedPlace) }
                                    },
                                )

                                AppScreen.BusinessStudio -> BusinessStudioScreen(
                                    onBack = { navigateBack() },
                                    onNeedSubscribe = {
                                        nav.push(AppScreen.BusinessSubscribe)
                                    },
                                )

                                AppScreen.BusinessSubscribe -> BusinessSubscribeScreen(
                                    onBack = { navigateBack() },
                                    onPaid = {
                                        // Remount studio so subscription status refreshes.
                                        if (nav.current is AppScreen.BusinessSubscribe) nav.pop()
                                        if (nav.current is AppScreen.BusinessStudio) nav.pop()
                                        nav.push(AppScreen.BusinessStudio)
                                    },
                                )

                                is AppScreen.TourDetail -> TourDetailScreen(
                                    destination = current.destination,
                                    onBack = { navigateBack() },
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
                                    onBack = { navigateBack() },
                                    onOpenSaved = {
                                        mainTab = WanderlustNavTab.Saved
                                        nav.popToMain(WanderlustNavTab.Saved)
                                    },
                                )

                                AppScreen.Admin -> AdminScreen(
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = toggleTheme,
                                    onBack = { navigateBack() },
                                    onOpenBookings = { nav.push(AppScreen.MyTrips) },
                                    onExportData = { nav.push(AppScreen.ExportData) },
                                    onAddTour = { nav.push(AppScreen.AddTour) },
                                    onEditTour = { nav.push(AppScreen.EditTour) },
                                    onManageUsers = { nav.push(AppScreen.ManageUsers) },
                                    onOpenAnalytics = { nav.push(AppScreen.Analytics) },
                                )

                                AppScreen.AddTour -> AddTourScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.EditTour -> EditTourScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.ManageUsers -> ManageUsersScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.Analytics -> AnalyticsScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.EditProfile -> EditProfileScreen(
                                    onBack = { navigateBack() },
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
                                    onBack = { navigateBack() },
                                )

                                is AppScreen.LegalDocument -> LegalDocumentScreen(
                                    type = current.type,
                                    onBack = { navigateBack() },
                                )

                                AppScreen.About -> AboutScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.HelpCenter -> HelpCenterScreen(
                                    onBack = { navigateBack() },
                                    onGoHome = {
                                        mainTab = WanderlustNavTab.Home
                                        nav.popToMain(WanderlustNavTab.Home)
                                    },
                                )

                                AppScreen.ExportData -> ExportDataScreen(
                                    onBack = { navigateBack() },
                                )

                                AppScreen.AddSavedPlace -> AddSavedPlaceScreen(
                                    onBack = { navigateBack() },
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

    companion object {
        private const val EXIT_CONFIRM_WINDOW_MS = 2_000L
    }
}
