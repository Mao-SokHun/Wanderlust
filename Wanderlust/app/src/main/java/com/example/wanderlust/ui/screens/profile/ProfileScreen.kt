package com.example.wanderlust.ui.screens.profile

import com.example.wanderlust.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.ui.components.ProfileAvatar
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.LoginRequiredPanel
import com.example.wanderlust.ui.components.SettingsSectionTitle
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.ThemeToggleButton
import com.example.wanderlust.ui.components.WanderlustBrand
@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenChangePassword: () -> Unit,
    onOpenSavedPlans: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenBusinessStudio: () -> Unit = {},
    onLogout: () -> Unit,
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
) {
    if (!SessionManager.isLoggedIn()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WanderlustBrand()
                ThemeToggleButton(isDark = isDarkTheme, onToggle = onToggleTheme)
            }
            LoginRequiredPanel(onSignIn = onSignIn, onRegister = onRegister)
        }
        return
    }

    val name = SessionManager.userName ?: "Explorer"
    val email = SessionManager.userEmail.orEmpty()
    val bio = SessionManager.userBio
    val city = SessionManager.userCity
    val phone = SessionManager.userPhone
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 8.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WanderlustBrand()
                    ThemeToggleButton(isDark = isDarkTheme, onToggle = onToggleTheme)
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 72.dp),
            ) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileAvatar(size = 64.dp, displayName = name)
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            stringApp(R.string.profile_tier),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (email.isNotBlank()) {
                            Text(
                                email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        if (city.isNotBlank() || phone.isNotBlank()) {
                            Text(
                                listOfNotNull(
                                    city.takeIf { it.isNotBlank() },
                                    phone.takeIf { it.isNotBlank() },
                                ).joinToString(" · "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        if (bio.isNotBlank()) {
                            Text(
                                bio,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SettingsSectionTitle(stringApp(R.string.profile_section_account))
        ProfileMenuRow(
            Icons.Default.Person,
            stringApp(R.string.profile_edit),
            stringApp(R.string.profile_edit_sub),
            onOpenEditProfile,
        )
        ProfileMenuRow(
            Icons.Default.Lock,
            stringApp(R.string.profile_change_password),
            stringApp(R.string.profile_change_password_sub),
            onOpenChangePassword,
        )
        ProfileMenuRow(
            Icons.Default.Bookmark,
            stringApp(R.string.profile_bookings),
            stringLocalized(R.string.profile_bookings_sub, R.string.profile_bookings_sub_kh),
            onOpenSavedPlans,
        )
        if (SessionManager.userRole == "BUSINESS" || SessionManager.isAdmin()) {
            ProfileMenuRow(
                Icons.Default.Storefront,
                stringLocalized(R.string.business_open_studio, R.string.business_open_studio_kh),
                stringLocalized(R.string.business_studio_sub, R.string.business_studio_sub_kh),
                onOpenBusinessStudio,
            )
        }
        ProfileMenuRow(
            Icons.Default.Settings,
            stringApp(R.string.profile_settings),
            stringApp(R.string.settings_section_preferences),
            onOpenSettings,
        )

        SettingsSectionTitle(stringApp(R.string.profile_section_support))
        ProfileMenuRow(
            Icons.AutoMirrored.Filled.Help,
            stringApp(R.string.profile_help),
            stringApp(R.string.profile_help_sub),
            onOpenHelp,
        )

        SettingsSectionTitle(stringApp(R.string.profile_section_legal))
        ProfileMenuRow(
            Icons.Default.Policy,
            stringApp(R.string.privacy_policy_title),
            stringApp(R.string.profile_privacy_sub),
            onOpenPrivacy,
        )
        ProfileMenuRow(
            Icons.Default.Description,
            stringApp(R.string.terms_title),
            stringApp(R.string.profile_terms_sub),
            onOpenTerms,
        )

        SettingsSectionTitle(stringApp(R.string.profile_section_app))
        ProfileMenuRow(
            Icons.Default.Info,
            stringApp(R.string.about_title),
            stringApp(R.string.about_tagline),
            onOpenAbout,
            showDivider = false,
        )

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
            Text(stringApp(R.string.profile_logout), modifier = Modifier.padding(start = 8.dp))
        }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean = true,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (showDivider) {
            HorizontalDivider(Modifier.padding(top = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    }
}
