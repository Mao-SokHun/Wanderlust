package com.example.wanderlust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.ui.components.ProfileAvatar
import com.example.wanderlust.ui.components.ProfileInfoRow
import com.example.wanderlust.ui.components.SettingsNavRow
import com.example.wanderlust.ui.components.SettingsSectionTitle
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.viewmodel.EditProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val accountType = stringResource(R.string.account_type_user)
    val focusManager = LocalFocusManager.current
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        val msg = state.successMessage ?: return@LaunchedEffect
        snackbar.showSnackbar(msg)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            delay(650)
            viewModel.clearSavedFlag()
            onBack()
        }
    }

    StickyScrollScreen(
        title = stringResource(R.string.edit_profile_title),
        onBack = onBack,
        modifier = modifier,
        bottomPadding = 28.dp,
    ) {
        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileAvatar(
                    size = 108.dp,
                    displayName = state.name.ifBlank { state.email },
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    state.name.ifBlank { stringResource(R.string.edit_profile_your_name) },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                if (state.email.isNotBlank()) {
                    Text(
                        state.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Text(
                    stringResource(R.string.edit_profile_photo_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        SettingsSectionTitle(stringResource(R.string.edit_profile_section_personal))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.label_name)) },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    supportingText = {
                        Text("${state.name.length}/60")
                    },
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.label_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    singleLine = true,
                    supportingText = {
                        Text(stringResource(R.string.edit_profile_email_hint))
                    },
                )

                OutlinedTextField(
                    value = state.bio,
                    onValueChange = viewModel::onBioChange,
                    label = { Text(stringResource(R.string.label_bio)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    placeholder = {
                        Text(stringResource(R.string.edit_profile_bio_placeholder))
                    },
                    supportingText = {
                        Text("${state.bio.length}/280")
                    },
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        SettingsSectionTitle(stringResource(R.string.edit_profile_section_account))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                ProfileInfoRow(
                    label = stringResource(R.string.label_account_type),
                    value = accountType,
                    showDivider = false,
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        SettingsSectionTitle(stringResource(R.string.edit_profile_section_security))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            SettingsNavRow(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.profile_change_password),
                subtitle = stringResource(R.string.profile_change_password_sub),
                onClick = onChangePassword,
                showDivider = false,
            )
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.save()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(
                    stringResource(R.string.btn_save_profile),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        SnackbarHost(hostState = snackbar)
    }
}
