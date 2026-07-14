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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit,
    onGoHome: () -> Unit,
) {
    StickyScrollScreen(
        title = stringResource(R.string.profile_help),
        onBack = onBack,
    ) {
        FaqCard(
            "How do I save a place?",
            "Sign in, open a suggested destination and tap Save place — or go to Saved → Add your own place to type an address, open Google Maps, and paste a link or coordinates.",
        )
        FaqCard(
            "How do I remove a saved place?",
            "Open Saved, tap a place, then use Remove from saved on the detail screen.",
        )
        FaqCard(
            "Does Wanderlust handle booking or payment?",
            "No. The app only suggests Cambodia places to explore and save — no checkout or payment.",
        )
        FaqCard(
            "How can I reset my password?",
            "On the login screen tap Forgot password, enter your email, then follow the reset steps.",
        )

        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text("Go to Home")
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
