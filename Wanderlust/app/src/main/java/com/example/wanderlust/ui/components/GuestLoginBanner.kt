package com.example.wanderlust.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.R
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.locale.stringLocalized

@Composable
fun GuestLoginBanner(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (GuestAccess.isLoggedIn()) return

    StitchGhostCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(
                stringLocalized(R.string.guest_banner_title, R.string.guest_banner_title_kh),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.guest_banner_body, GuestAccess.PREVIEW_LIMIT, GuestAccess.totalPlaceCount()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            )
            Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.guest_banner_sign_in))
            }
        }
    }
}
