package com.example.wanderlust

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.ui.components.ProfileAvatar
import com.example.wanderlust.ui.components.SettingsNavRow
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.viewmodel.EditProfileViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val focusManager = LocalFocusManager.current
    val snackbar = remember { SnackbarHostState() }
    val fieldShape = RoundedCornerShape(14.dp)
    val fieldFill = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

    val genderOptions = listOf(
        "" to stringApp(R.string.gender_prefer_not),
        "female" to stringApp(R.string.gender_female),
        "male" to stringApp(R.string.gender_male),
        "other" to stringApp(R.string.gender_other),
    )
    val travelOptions = listOf(
        "" to stringApp(R.string.travel_style_any),
        "solo" to stringApp(R.string.travel_style_solo),
        "couple" to stringApp(R.string.travel_style_couple),
        "family" to stringApp(R.string.travel_style_family),
        "friends" to stringApp(R.string.travel_style_friends),
    )

    LaunchedEffect(state.successMessage) {
        val msg = state.successMessage ?: return@LaunchedEffect
        snackbar.showSnackbar(msg)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            delay(250)
            viewModel.clearSavedFlag()
            onBack()
        }
    }

    StickyScrollScreen(
        title = stringApp(R.string.edit_profile_title),
        onBack = onBack,
        modifier = modifier,
        bottomPadding = 32.dp,
    ) {
        // Header
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 22.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileAvatar(
                size = 88.dp,
                displayName = state.name.ifBlank { state.email },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                state.name.ifBlank { stringApp(R.string.edit_profile_your_name) },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (state.email.isNotBlank()) {
                Text(
                    state.email,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            val cityPhone = listOfNotNull(
                state.city.takeIf { it.isNotBlank() },
                state.phone.takeIf { it.isNotBlank() },
            ).joinToString(" · ")
            if (cityPhone.isNotBlank()) {
                Text(
                    cityPhone,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionLabel(stringApp(R.string.edit_profile_section_personal))

        ProfileField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = stringApp(R.string.label_name),
            leading = { Icon(Icons.Default.Person, null) },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
            supporting = "${state.name.length}/60",
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            value = state.email,
            onValueChange = {},
            label = stringApp(R.string.label_email),
            enabled = false,
            readOnly = true,
            shape = fieldShape,
            fill = fieldFill,
            supporting = stringApp(R.string.edit_profile_email_hint),
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = stringApp(R.string.label_phone),
            leading = { Icon(Icons.Default.Phone, null) },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            ),
            placeholder = stringApp(R.string.edit_profile_phone_hint),
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            value = state.city,
            onValueChange = viewModel::onCityChange,
            label = stringApp(R.string.label_city),
            leading = { Icon(Icons.Default.Place, null) },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
            placeholder = stringApp(R.string.edit_profile_city_hint),
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            value = state.nationality,
            onValueChange = viewModel::onNationalityChange,
            label = stringApp(R.string.label_nationality),
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            value = state.birthDate,
            onValueChange = viewModel::onBirthDateChange,
            label = stringApp(R.string.label_birth_date),
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            placeholder = stringApp(R.string.edit_profile_birth_hint),
            supporting = stringApp(R.string.edit_profile_birth_support),
        )

        Spacer(Modifier.height(14.dp))
        ChipLabel(stringApp(R.string.label_gender))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            genderOptions.forEach { (value, label) ->
                FilterChip(
                    selected = state.gender == value,
                    onClick = { viewModel.onGenderChange(value) },
                    label = {
                        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.SansSerif))
                    },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    ),
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        ProfileField(
            value = state.bio,
            onValueChange = viewModel::onBioChange,
            label = stringApp(R.string.label_bio),
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            singleLine = false,
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            placeholder = stringApp(R.string.edit_profile_bio_placeholder),
            supporting = "${state.bio.length}/280",
        )

        Spacer(Modifier.height(22.dp))
        SectionLabel(stringApp(R.string.edit_profile_section_travel))

        ChipLabel(stringApp(R.string.label_travel_style))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            travelOptions.forEach { (value, label) ->
                FilterChip(
                    selected = state.travelStyle == value,
                    onClick = { viewModel.onTravelStyleChange(value) },
                    label = {
                        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.SansSerif))
                    },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    ),
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        ProfileField(
            value = state.emergencyContact,
            onValueChange = viewModel::onEmergencyContactChange,
            label = stringApp(R.string.label_emergency_contact),
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            placeholder = stringApp(R.string.edit_profile_emergency_hint),
            supporting = stringApp(R.string.edit_profile_emergency_support),
        )

        Spacer(Modifier.height(22.dp))
        SectionLabel(stringApp(R.string.edit_profile_section_account))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            MetaRow(
                stringApp(R.string.label_account_type),
                if (SessionManager.isAdmin()) {
                    stringApp(R.string.account_type_admin)
                } else {
                    stringApp(R.string.account_type_user)
                },
            )
            if (state.nationality.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                MetaRow(stringApp(R.string.label_nationality), state.nationality)
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionLabel(stringApp(R.string.edit_profile_section_security))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            SettingsNavRow(
                icon = Icons.Default.Lock,
                title = stringApp(R.string.profile_change_password),
                subtitle = stringApp(R.string.profile_change_password_sub),
                onClick = onChangePassword,
                showDivider = false,
            )
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif),
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.save()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(
                    stringApp(R.string.btn_save_profile),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        SnackbarHost(hostState = snackbar)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun ChipLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun MetaRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.SansSerif),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    shape: RoundedCornerShape,
    fill: Color,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    leading: (@Composable (() -> Unit))? = null,
    placeholder: String? = null,
    supporting: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif))
        },
        placeholder = placeholder?.let {
            {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        },
        leadingIcon = leading,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        supportingText = supporting?.let {
            {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.SansSerif),
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fill,
            unfocusedContainerColor = fill,
            disabledContainerColor = fill.copy(alpha = 0.7f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
