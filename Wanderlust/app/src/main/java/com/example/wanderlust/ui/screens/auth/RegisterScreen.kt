package com.example.wanderlust.ui.screens.auth

import com.example.wanderlust.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.WanderlustBrand
import com.example.wanderlust.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val scheme = MaterialTheme.colorScheme
    val fieldShape = RoundedCornerShape(14.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = scheme.primaryContainer,
        unfocusedBorderColor = scheme.outlineVariant.copy(alpha = 0.55f),
        focusedContainerColor = scheme.surface,
        unfocusedContainerColor = scheme.surface,
    )

    LaunchedEffect(state.registerSuccess) {
        if (state.registerSuccess) {
            viewModel.resetSuccess()
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.tertiary.copy(alpha = 0.12f),
                        scheme.background,
                        scheme.background,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            WanderlustBrand()
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringApp(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = scheme.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringLocalized(R.string.register_hint, R.string.register_hint_kh),
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))

            StitchGhostCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        stringLocalized(R.string.register_account_type, R.string.register_account_type_kh),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = state.role == "USER",
                            onClick = { viewModel.onRoleChange("USER") },
                            label = {
                                Text(stringLocalized(R.string.register_as_traveler, R.string.register_as_traveler_kh))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = scheme.primaryContainer,
                                selectedLabelColor = scheme.onPrimaryContainer,
                            ),
                        )
                        FilterChip(
                            selected = state.role == "BUSINESS",
                            onClick = { viewModel.onRoleChange("BUSINESS") },
                            label = {
                                Text(stringLocalized(R.string.register_as_business, R.string.register_as_business_kh))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = scheme.primaryContainer,
                                selectedLabelColor = scheme.onPrimaryContainer,
                            ),
                        )
                    }
                    if (state.role == "BUSINESS") {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringLocalized(R.string.register_business_hint, R.string.register_business_hint_kh),
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = state.businessSubtype == "TOURS",
                                onClick = { viewModel.onBusinessSubtypeChange("TOURS") },
                                label = {
                                    Text(stringLocalized(R.string.register_biz_tours, R.string.register_biz_tours_kh))
                                },
                            )
                            FilterChip(
                                selected = state.businessSubtype == "TRANSPORT",
                                onClick = { viewModel.onBusinessSubtypeChange("TRANSPORT") },
                                label = {
                                    Text(
                                        stringLocalized(
                                            R.string.register_biz_transport,
                                            R.string.register_biz_transport_kh,
                                        ),
                                    )
                                },
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text(stringApp(R.string.label_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = fieldShape,
                        colors = fieldColors,
                    )
                    if (state.role == "BUSINESS") {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.companyName,
                            onValueChange = viewModel::onCompanyNameChange,
                            label = {
                                Text(stringLocalized(R.string.register_company_name, R.string.register_company_name_kh))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = fieldShape,
                            colors = fieldColors,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    val duplicateEmailMsg = stringLocalized(
                        R.string.register_email_duplicate,
                        R.string.register_email_duplicate_kh,
                    )
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text(stringApp(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = state.isEmailDuplicate,
                        supportingText = if (state.isEmailDuplicate) {
                            { Text(duplicateEmailMsg) }
                        } else {
                            null
                        },
                        shape = fieldShape,
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text(stringApp(R.string.label_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = fieldShape,
                        colors = fieldColors,
                    )
                    if (state.errorMessage != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = state.errorMessage,
                            color = scheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = viewModel::register,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primaryContainer,
                            contentColor = scheme.onPrimaryContainer,
                        ),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(22.dp),
                                color = scheme.onPrimaryContainer,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                stringApp(R.string.btn_create_account),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            com.example.wanderlust.ui.components.SocialAuthButtons(
                enabled = !state.isLoading,
                onGoogleToken = viewModel::registerWithGoogle,
                onFacebookToken = viewModel::registerWithFacebook,
                onError = viewModel::setError,
            )

            Spacer(Modifier.height(14.dp))
            TextButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringApp(R.string.register_sign_in_prompt),
                    color = scheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(stringApp(R.string.btn_back))
            }
        }
    }
}
