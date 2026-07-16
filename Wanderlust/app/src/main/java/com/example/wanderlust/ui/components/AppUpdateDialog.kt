package com.example.wanderlust.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.wanderlust.R
import com.example.wanderlust.data.repository.AppUpdateAvailability
import com.example.wanderlust.locale.stringApp

@Composable
fun AppUpdateDialog(
    update: AppUpdateAvailability,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            if (!update.forceUpdate) onDismiss()
        },
        title = {
            Text(
                stringApp(
                    if (update.forceUpdate) R.string.update_required_title else R.string.update_available_title,
                ),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(stringApp(R.string.update_available_body, update.info.versionName))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(update.downloadUrl)),
                        )
                    }
                    if (!update.forceUpdate) onDismiss()
                },
            ) {
                Text(stringApp(R.string.update_download), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (!update.forceUpdate) {
                TextButton(onClick = onDismiss) {
                    Text(stringApp(R.string.update_later))
                }
            }
        },
    )
}
