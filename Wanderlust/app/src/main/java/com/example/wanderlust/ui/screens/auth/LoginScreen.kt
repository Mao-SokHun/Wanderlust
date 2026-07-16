package com.example.wanderlust.ui.screens.auth

import com.example.wanderlust.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.WanderlustBrand
import com.example.wanderlust.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
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

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            viewModel.resetSuccess()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.primaryContainer.copy(alpha = 0.18f),
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
            Spacer(Modifier.height(28.dp))
            Text(
                text = stringApp(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = scheme.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringApp(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))

            StitchGhostCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text(stringApp(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
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
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = onForgotPassword,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(stringApp(R.string.forgot_password_link))
                    }
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = viewModel::login,
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
                                stringApp(R.string.btn_login),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringApp(R.string.login_sign_up_prompt),
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
