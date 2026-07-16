package com.example.wanderlust.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.R
import com.example.wanderlust.data.repository.AppUpdateAvailability
import com.example.wanderlust.data.repository.AppUpdateRepository
import com.example.wanderlust.locale.stringApp

@Composable
fun AppUpdateDialog(
    update: AppUpdateAvailability,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val installed = AppUpdateRepository.installedVersionLabel()
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
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    stringApp(
                        R.string.update_available_compare,
                        installed,
                        update.info.versionName,
                        update.info.versionCode,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    stringApp(R.string.update_available_body, update.info.versionName, update.info.versionCode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (update.info.releaseNotes.isNotBlank()) {
                    Text(
                        update.info.releaseNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    stringApp(R.string.about_install_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
                Text(stringApp(R.string.about_install_update), fontWeight = FontWeight.Bold)
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
