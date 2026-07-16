package com.example.wanderlust.ui.screens.info

import com.example.wanderlust.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.AdminContact
import com.example.wanderlust.data.remote.ApiConnection
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.AppVersionUpdateCard
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.WanderlustLogo
import com.example.wanderlust.ui.components.WanderlustLogoStyle
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val versionLabel = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    fun openUri(uri: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        }
    }

    StickyScrollScreen(
        title = stringApp(R.string.about_title),
        onBack = onBack,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WanderlustLogo(size = 80.dp, style = WanderlustLogoStyle.Badge)
            Spacer(Modifier.height(14.dp))
            Text(
                stringApp(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = scheme.primary,
            )
            Text(
                stringApp(R.string.about_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringApp(R.string.about_version, versionLabel),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = scheme.tertiary,
            )
        }

        Spacer(Modifier.height(16.dp))
        AppVersionUpdateCard()

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                val base = ApiConnection.activeUrl()?.trimEnd('/')
                val url = if (base.isNullOrBlank()) {
                    "https://wanderlust-api-dm3y.onrender.com/download/"
                } else {
                    "$base/download/"
                }
                openUri(url)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(stringApp(R.string.about_get_on_devices))
        }

        Spacer(Modifier.height(18.dp))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    stringApp(R.string.about_what_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = scheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringApp(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    stringApp(R.string.about_developer_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = scheme.primary,
                )
                Text(
                    AdminContact.OWNER_NAME,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    if (AppLocale.isKhmer) AdminContact.OWNER_ROLE_KH else AdminContact.OWNER_ROLE,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
                Text(
                    if (AppLocale.isKhmer) AdminContact.PROJECT_NOTE_KH else AdminContact.PROJECT_NOTE,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        val subject = Uri.encode("Wanderlust app — contact developer")
                        openUri("mailto:${AdminContact.EMAIL}?subject=$subject")
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.primaryContainer,
                        contentColor = scheme.onPrimaryContainer,
                    ),
                ) {
                    Text(stringApp(R.string.about_email_developer), fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { openUri(AdminContact.TELEGRAM_URL) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringApp(R.string.about_telegram_developer))
                }
                AdminContact.PHONES.forEach { phone ->
                    OutlinedButton(
                        onClick = { openUri("tel:${phone.filter { it.isDigit() || it == '+' }}") },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("${stringApp(R.string.about_call_developer)} · $phone")
                    }
                }
                Text(
                    AdminContact.EMAIL,
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.tertiary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    "@${AdminContact.TELEGRAM_USERNAME}",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    AdminContact.PHONES.joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            stringApp(R.string.about_credit),
            style = MaterialTheme.typography.labelSmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            stringApp(R.string.about_copyright),
            style = MaterialTheme.typography.labelSmall,
            color = scheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textAlign = TextAlign.Center,
        )
    }
}
