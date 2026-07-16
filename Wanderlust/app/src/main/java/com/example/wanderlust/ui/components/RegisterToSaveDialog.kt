package com.example.wanderlust.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.wanderlust.R
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.locale.stringLocalized

/** Shown when a guest tries to save a place — browse stays free. */
@Composable
fun RegisterToSaveDialog(
    onDismiss: () -> Unit,
    onRegister: () -> Unit,
    onSignIn: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringLocalized(R.string.guest_save_alert_title, R.string.guest_save_alert_title_kh))
        },
        text = {
            Text(stringLocalized(R.string.guest_save_alert_body, R.string.guest_save_alert_body_kh))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onRegister()
                },
            ) {
                Text(stringApp(R.string.btn_register))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onSignIn()
                },
            ) {
                Text(stringApp(R.string.btn_login))
            }
        },
    )
}
