package com.example.wanderlust.ui.screens.tours

import com.example.wanderlust.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.unit.dp
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen

/** Saved trip ideas — no booking or payment */
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    onOpenSaved: () -> Unit,
) {
    StickyScrollScreen(
        title = stringApp(R.string.profile_bookings),
        onBack = onBack,
    ) {
        Text(
            stringLocalized(R.string.saved_plan_message, R.string.saved_plan_message_kh),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        StitchGhostCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenSaved),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringApp(R.string.saved_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringLocalized(R.string.saved_subtitle, R.string.saved_subtitle_kh),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
