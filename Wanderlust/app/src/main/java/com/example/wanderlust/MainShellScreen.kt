package com.example.wanderlust

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.ui.components.WanderlustBottomNav
import com.example.wanderlust.ui.components.WanderlustNavTab
import com.example.wanderlust.viewmodel.ExploreViewModel
import com.example.wanderlust.viewmodel.FavoritesViewModel

private sealed class ProfileOverlay {
    data object EditProfile : ProfileOverlay()
    data object ChangePassword : ProfileOverlay()
}

@Composable
fun MainShellScreen(
    selectedTab: WanderlustNavTab,
    onTabChange: (WanderlustNavTab) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onDestinationClick: (DestinationCard) -> Unit,
    exploreInitialQuery: String = "",
    exploreInitialCategory: String? = null,
    savedRefreshKey: Int = 0,
    onSearch: (String) -> Unit,
    onViewAllDestinations: () -> Unit = {},
    onCategoryExplore: (String?) -> Unit = {},
    onOpenSavedPlans: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAbout: () -> Unit,
    onLogout: () -> Unit,
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
    onAddSavedPlace: () -> Unit = {},
    exploreViewModel: ExploreViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
) {
    var profileOverlay by remember { mutableStateOf<ProfileOverlay?>(null) }
    var changePasswordReturnsToEdit by remember { mutableStateOf(false) }
    var profileRevision by remember { mutableIntStateOf(0) }

    BackHandler(enabled = profileOverlay != null) {
        when (profileOverlay) {
            ProfileOverlay.ChangePassword -> {
                profileOverlay = if (changePasswordReturnsToEdit) {
                    ProfileOverlay.EditProfile
                } else {
                    null
                }
            }
            ProfileOverlay.EditProfile -> {
                profileOverlay = null
                profileRevision++
            }
            null -> Unit
        }
    }

    fun openChangePassword(fromEditProfile: Boolean) {
        changePasswordReturnsToEdit = fromEditProfile
        profileOverlay = ProfileOverlay.ChangePassword
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            WanderlustNavTab.Home -> HomeScreen(
                onDestinationClick = onDestinationClick,
                onExploreAll = onViewAllDestinations,
                onSearch = onSearch,
                onCategoryExplore = onCategoryExplore,
                onSignIn = onSignIn,
                onPlaceSaved = { favoritesViewModel.refresh() },
            )
            WanderlustNavTab.Explore -> ExploreScreen(
                initialQuery = exploreInitialQuery,
                initialCategory = exploreInitialCategory,
                onTourClick = onDestinationClick,
                onSignIn = onSignIn,
                viewModel = exploreViewModel,
            )
            WanderlustNavTab.Saved -> SavedScreen(
                onDestinationClick = onDestinationClick,
                refreshKey = savedRefreshKey,
                onSignIn = onSignIn,
                onRegister = onRegister,
                onAddPlace = onAddSavedPlace,
                viewModel = favoritesViewModel,
            )
            WanderlustNavTab.Profile -> key(profileRevision) {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    onOpenEditProfile = { profileOverlay = ProfileOverlay.EditProfile },
                    onOpenChangePassword = { openChangePassword(fromEditProfile = false) },
                    onOpenSavedPlans = onOpenSavedPlans,
                    onOpenSettings = onOpenSettings,
                    onOpenHelp = onOpenHelp,
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenTerms = onOpenTerms,
                    onOpenAbout = onOpenAbout,
                    onLogout = onLogout,
                    onSignIn = onSignIn,
                    onRegister = onRegister,
                )
            }
        }

        WanderlustBottomNav(
            selected = selectedTab,
            modifier = Modifier.align(Alignment.BottomCenter),
            onHome = { onTabChange(WanderlustNavTab.Home) },
            onExplore = { onTabChange(WanderlustNavTab.Explore) },
            onSaved = { onTabChange(WanderlustNavTab.Saved) },
            onProfile = { onTabChange(WanderlustNavTab.Profile) },
        )

        when (profileOverlay) {
            ProfileOverlay.EditProfile -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                EditProfileScreen(
                    onBack = {
                        profileOverlay = null
                        profileRevision++
                    },
                    onChangePassword = { openChangePassword(fromEditProfile = true) },
                )
            }
            ProfileOverlay.ChangePassword -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                ChangePasswordScreen(
                    onBack = {
                        profileOverlay = if (changePasswordReturnsToEdit) {
                            ProfileOverlay.EditProfile
                        } else {
                            null
                        }
                    },
                )
            }
            null -> Unit
        }
    }
}
