package com.example.wanderlust

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                )
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
                            stringResource(R.string.profile_tier),
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
        SettingsSectionTitle(stringResource(R.string.profile_section_account))
        ProfileMenuRow(
            Icons.Default.Person,
            stringResource(R.string.profile_edit),
            stringResource(R.string.profile_edit_sub),
            onOpenEditProfile,
        )
        ProfileMenuRow(
            Icons.Default.Lock,
            stringResource(R.string.profile_change_password),
            stringResource(R.string.profile_change_password_sub),
            onOpenChangePassword,
        )
        ProfileMenuRow(
            Icons.Default.Bookmark,
            stringResource(R.string.profile_bookings),
            stringLocalized(R.string.profile_bookings_sub, R.string.profile_bookings_sub_kh),
            onOpenSavedPlans,
        )
        ProfileMenuRow(
            Icons.Default.Settings,
            stringResource(R.string.profile_settings),
            stringLocalized(R.string.settings_section_preferences, R.string.settings_section_preferences),
            onOpenSettings,
        )

        SettingsSectionTitle(stringResource(R.string.profile_section_support))
        ProfileMenuRow(
            Icons.AutoMirrored.Filled.Help,
            stringResource(R.string.profile_help),
            "FAQs and contact",
            onOpenHelp,
        )

        SettingsSectionTitle(stringResource(R.string.profile_section_legal))
        ProfileMenuRow(
            Icons.Default.Policy,
            stringResource(R.string.privacy_policy_title),
            "How we handle your data",
            onOpenPrivacy,
        )
        ProfileMenuRow(
            Icons.Default.Description,
            stringResource(R.string.terms_title),
            "Rules for using Wanderlust",
            onOpenTerms,
        )

        SettingsSectionTitle(stringResource(R.string.profile_section_app))
        ProfileMenuRow(
            Icons.Default.Info,
            stringResource(R.string.about_title),
            stringResource(R.string.about_tagline),
            onOpenAbout,
            showDivider = false,
        )

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
            Text(stringResource(R.string.profile_logout), modifier = Modifier.padding(start = 8.dp))
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
