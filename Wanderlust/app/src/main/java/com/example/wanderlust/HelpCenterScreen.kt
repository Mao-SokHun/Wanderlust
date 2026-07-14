package com.example.wanderlust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit,
    onGoHome: () -> Unit,
) {
    StickyScrollScreen(
        title = stringApp(R.string.profile_help),
        onBack = onBack,
    ) {
        FaqCard(
            stringApp(R.string.help_faq_save_q),
            stringApp(R.string.help_faq_save_a),
        )
        FaqCard(
            stringApp(R.string.help_faq_remove_q),
            stringApp(R.string.help_faq_remove_a),
        )
        FaqCard(
            stringApp(R.string.help_faq_booking_q),
            stringApp(R.string.help_faq_booking_a),
        )
        FaqCard(
            stringApp(R.string.help_faq_password_q),
            stringApp(R.string.help_faq_password_a),
        )

        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text(stringApp(R.string.btn_go_home))
        }
    }
}

@Composable
private fun FaqCard(title: String, answer: String) {
    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(answer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
