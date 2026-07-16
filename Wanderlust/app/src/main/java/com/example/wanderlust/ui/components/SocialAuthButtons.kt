package com.example.wanderlust.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.R
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.util.SocialAuthHelper
import com.example.wanderlust.util.SocialAuthResult
import kotlinx.coroutines.launch

@Composable
fun SocialAuthButtons(
    enabled: Boolean = true,
    onGoogleToken: (String) -> Unit,
    onFacebookToken: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as? Activity
    val socialFailed = stringApp(R.string.auth_social_failed)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                stringLocalized(R.string.auth_or_continue, R.string.auth_or_continue_kh),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        when (val result = SocialAuthHelper.signInWithGoogle(context)) {
                            is SocialAuthResult.Google -> onGoogleToken(result.idToken)
                            is SocialAuthResult.Error -> onError(result.message)
                            else -> onError(socialFailed)
                        }
                    }
                },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    stringApp(R.string.auth_google),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(
                onClick = {
                    if (activity == null) {
                        onError("Facebook sign-in needs an Activity context.")
                        return@OutlinedButton
                    }
                    scope.launch {
                        when (val result = SocialAuthHelper.signInWithFacebook(activity)) {
                            is SocialAuthResult.Facebook -> onFacebookToken(result.accessToken)
                            is SocialAuthResult.Error -> onError(result.message)
                            else -> onError(socialFailed)
                        }
                    }
                },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    stringApp(R.string.auth_facebook),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            stringLocalized(R.string.welcome_social_hint, R.string.welcome_social_hint_kh),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
