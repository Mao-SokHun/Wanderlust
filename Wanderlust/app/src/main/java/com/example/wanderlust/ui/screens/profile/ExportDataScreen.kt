package com.example.wanderlust.ui.screens.profile

import com.example.wanderlust.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ExportDataScreen(onBack: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    var exported by remember { mutableStateOf(false) }
    val exportText = remember {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        "report_generated_at,$now\nmetric,value\nactive_tours,1284\nglobal_users,42800\nsaved_places,312\n"
    }

    StickyScrollScreen(
        title = stringApp(R.string.admin_export_title),
        onBack = onBack,
    ) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(stringApp(R.string.export_csv_preview), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(exportText, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                clipboard.setText(AnnotatedString(exportText))
                exported = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringApp(R.string.export_copy_csv))
        }
        if (exported) {
            Text(
                stringApp(R.string.admin_export_copied),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
