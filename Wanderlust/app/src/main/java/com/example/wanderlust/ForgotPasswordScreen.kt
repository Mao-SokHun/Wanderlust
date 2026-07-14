package com.example.wanderlust

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetPassword: (email: String, token: String) -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel(),
) {
    val state = viewModel.uiState

    StickyScrollScreen(
        title = stringApp(R.string.forgot_password_title),
        onBack = onBack,
        bottomPadding = 24.dp,
    ) {
        Text(
            stringApp(R.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text(stringApp(R.string.label_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        state.successMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
        state.resetToken?.let { code ->
            Spacer(Modifier.height(8.dp))
            Text(
                stringApp(R.string.forgot_password_code, code),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = viewModel::requestCode,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text(stringApp(R.string.btn_send_reset_code))
            }
        }
        if (state.resetToken != null) {
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { onResetPassword(state.email.trim(), state.resetToken!!) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringApp(R.string.btn_enter_new_password))
            }
        }
    }
}
