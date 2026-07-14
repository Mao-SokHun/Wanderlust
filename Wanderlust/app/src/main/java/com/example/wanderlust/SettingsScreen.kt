package com.example.wanderlust

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.SettingsLanguageRow
import com.example.wanderlust.ui.components.SettingsNavRow
import com.example.wanderlust.ui.components.SettingsSectionTitle
import com.example.wanderlust.ui.components.SettingsToggleRow
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val loggedIn = SessionManager.isLoggedIn()
    // Observe locale so this screen (and callers keying off it) recompose.
    val lang = AppLocale.code
    val isKhmer = lang != AppLocale.EN

    StickyScrollScreen(
        title = stringLocalized(R.string.profile_settings, R.string.profile_settings_kh),
        onBack = onBack,
        headerTrailing = {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp))
            }
        },
    ) {
        state.errorMessage?.let { msg ->
            Text(
                msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }

        SettingsSectionTitle(stringLocalized(R.string.settings_section_preferences, R.string.settings_section_preferences_kh))
        SettingsLanguageRow(
            selectedIsKhmer = isKhmer,
            onSelectKhmer = {
                SessionManager.setLanguage("km")
                if (loggedIn) viewModel.saveLanguage("km")
            },
            onSelectEnglish = {
                SessionManager.setLanguage("en")
                if (loggedIn) viewModel.saveLanguage("en")
            },
        )
        SettingsToggleRow(
            title = if (isDarkTheme) {
                stringLocalized(R.string.theme_dark, R.string.theme_dark_kh)
            } else {
                stringLocalized(R.string.theme_light, R.string.theme_light_kh)
            },
            subtitle = stringLocalized(R.string.settings_theme_subtitle, R.string.settings_theme_subtitle_kh),
            checked = isDarkTheme,
            onCheckedChange = {
                val newDark = !isDarkTheme
                if (loggedIn) {
                    viewModel.saveTheme(newDark)
                }
                onToggleTheme()
            },
        )
        if (loggedIn) {
            SettingsToggleRow(
                title = stringLocalized(R.string.settings_notifications, R.string.settings_notifications_kh),
                subtitle = stringLocalized(
                    R.string.settings_notifications_sub,
                    R.string.settings_notifications_sub_kh,
                ),
                checked = state.notificationsEnabled,
                onCheckedChange = viewModel::onNotificationsChange,
            )
            SettingsToggleRow(
                title = stringLocalized(R.string.settings_location, R.string.settings_location_kh),
                subtitle = stringLocalized(R.string.settings_location_sub, R.string.settings_location_sub_kh),
                checked = state.locationEnabled,
                onCheckedChange = viewModel::onLocationChange,
            )
        }

        SettingsSectionTitle(stringApp(R.string.settings_section_legal))
        SettingsNavRow(
            Icons.Default.Policy,
            stringApp(R.string.privacy_policy_title),
            stringApp(R.string.settings_privacy_sub),
            onClick = onOpenPrivacy,
        )
        SettingsNavRow(
            Icons.Default.Description,
            stringApp(R.string.terms_title),
            stringApp(R.string.settings_terms_sub),
            onClick = onOpenTerms,
            showDivider = false,
        )

        SettingsSectionTitle(stringApp(R.string.settings_section_about))
        SettingsNavRow(
            Icons.Default.Info,
            stringApp(R.string.about_title),
            stringApp(R.string.about_version, "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
            onClick = onOpenAbout,
            showDivider = false,
        )
    }
}
