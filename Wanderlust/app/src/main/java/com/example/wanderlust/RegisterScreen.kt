package com.example.wanderlust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
) {
    val state = viewModel.uiState

    LaunchedEffect(state.registerSuccess) {
        if (state.registerSuccess) {
            viewModel.resetSuccess()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
    ) {
        Text(
            text = stringApp(R.string.register_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringLocalized(R.string.register_hint, R.string.register_hint_kh),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            stringLocalized(R.string.register_account_type, R.string.register_account_type_kh),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            )
            FilterChip(
                selected = state.role == "BUSINESS",
                onClick = { viewModel.onRoleChange("BUSINESS") },
                label = {
                    Text(stringLocalized(R.string.register_as_business, R.string.register_as_business_kh))
                },
            )
        }
        if (state.role == "BUSINESS") {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringLocalized(R.string.register_business_hint, R.string.register_business_hint_kh),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = { Text(stringApp(R.string.label_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        if (state.role == "BUSINESS") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.companyName,
                onValueChange = viewModel::onCompanyNameChange,
                label = {
                    Text(stringLocalized(R.string.register_company_name, R.string.register_company_name_kh))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(stringApp(R.string.label_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::register,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text(stringApp(R.string.btn_create_account))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) {
            Text(stringApp(R.string.register_sign_in_prompt))
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringApp(R.string.btn_back))
        }
    }
}
