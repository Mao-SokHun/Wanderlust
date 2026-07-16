package com.example.wanderlust.ui.screens.auth

import com.example.wanderlust.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.util.Validation
import com.example.wanderlust.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: ChangePasswordViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val focusManager = LocalFocusManager.current
    val snackbar = remember { SnackbarHostState() }
    val fieldShape = RoundedCornerShape(14.dp)
    val fieldFill = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage) {
        val msg = state.successMessage ?: return@LaunchedEffect
        snackbar.showSnackbar(msg)
        viewModel.clearSuccessMessage()
    }

    StickyScrollScreen(
        title = stringApp(R.string.change_password_title),
        onBack = onBack,
        bottomPadding = 32.dp,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 22.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                stringApp(R.string.change_password_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                stringApp(R.string.change_password_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(22.dp))
        Text(
            stringApp(R.string.edit_profile_section_security),
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        PasswordField(
            value = state.currentPassword,
            onValueChange = viewModel::onCurrentChange,
            label = stringApp(R.string.label_current_password),
            visible = showCurrent,
            onToggleVisible = { showCurrent = !showCurrent },
            showLabel = if (showCurrent) {
                stringApp(R.string.change_password_hide)
            } else {
                stringApp(R.string.change_password_show)
            },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            imeAction = ImeAction.Next,
            supporting = "${state.currentPassword.length}/${Validation.PASSWORD_MAX}",
        )
        Spacer(Modifier.height(10.dp))
        PasswordField(
            value = state.newPassword,
            onValueChange = viewModel::onNewChange,
            label = stringApp(R.string.label_new_password),
            visible = showNew,
            onToggleVisible = { showNew = !showNew },
            showLabel = if (showNew) {
                stringApp(R.string.change_password_hide)
            } else {
                stringApp(R.string.change_password_show)
            },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            imeAction = ImeAction.Next,
            supporting = stringApp(R.string.change_password_hint),
        )
        Spacer(Modifier.height(10.dp))
        PasswordField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmChange,
            label = stringApp(R.string.label_confirm_password),
            visible = showConfirm,
            onToggleVisible = { showConfirm = !showConfirm },
            showLabel = if (showConfirm) {
                stringApp(R.string.change_password_hide)
            } else {
                stringApp(R.string.change_password_show)
            },
            enabled = !state.isLoading,
            shape = fieldShape,
            fill = fieldFill,
            imeAction = ImeAction.Done,
            onDone = {
                focusManager.clearFocus()
                viewModel.changePassword()
            },
            supporting = when {
                state.confirmPassword.isNotEmpty() &&
                    state.newPassword == state.confirmPassword ->
                    stringApp(R.string.change_password_match)
                else -> "${state.confirmPassword.length}/${Validation.PASSWORD_MAX}"
            },
        )

        PasswordStrengthRow(password = state.newPassword)

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
                viewModel.changePassword()
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
                    stringApp(R.string.btn_change_password),
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
private fun PasswordStrengthRow(password: String) {
    if (password.isEmpty()) return
    val score = when {
        password.length >= 12 && password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() } -> 3
        password.length >= 8 && password.any { it.isDigit() || !it.isLetterOrDigit() } -> 2
        password.length >= Validation.PASSWORD_MIN -> 1
        else -> 0
    }
    val label = when (score) {
        3 -> stringApp(R.string.change_password_strength_strong)
        2 -> stringApp(R.string.change_password_strength_medium)
        1 -> stringApp(R.string.change_password_strength_fair)
        else -> stringApp(R.string.change_password_strength_weak)
    }
    val color = when (score) {
        3 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.tertiary
        1 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index < score) color
                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        ),
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
            ),
            color = color,
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    showLabel: String,
    shape: RoundedCornerShape,
    fill: Color,
    enabled: Boolean,
    imeAction: ImeAction,
    onDone: (() -> Unit)? = null,
    supporting: String? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.SansSerif))
        },
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = showLabel,
                )
            }
        },
        enabled = enabled,
        singleLine = true,
        shape = shape,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone?.invoke() },
        ),
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
