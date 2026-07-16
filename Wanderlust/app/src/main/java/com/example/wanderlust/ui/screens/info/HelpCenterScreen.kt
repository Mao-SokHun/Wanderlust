package com.example.wanderlust.ui.screens.info

import com.example.wanderlust.R

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.AdminContact
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.repository.SupportRepository
import com.example.wanderlust.locale.optionLabel
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

private val SUPPORT_TOPICS = listOf(
    "App crash / bug",
    "Login / account",
    "Business / subscription",
    "Listing / booking issue",
    "Other",
)

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit,
    onGoHome: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SupportRepository() }

    var telegramUrl by remember { mutableStateOf(AdminContact.TELEGRAM_URL) }
    var supportEmail by remember { mutableStateOf(AdminContact.EMAIL) }
    var supportPhones by remember { mutableStateOf(AdminContact.PHONES) }

    var topic by remember { mutableStateOf(SUPPORT_TOPICS[0]) }
    var message by remember { mutableStateOf("") }
    var replyEmail by remember { mutableStateOf(SessionManager.userEmail.orEmpty()) }
    var replyPhone by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    var formSuccess by remember { mutableStateOf<String?>(null) }
    val mailSubject = stringApp(R.string.help_mail_subject)
    val mailBodyPrefix = stringApp(R.string.help_mail_body)
    val reportSentMsg = stringApp(R.string.help_report_sent)

    LaunchedEffect(Unit) {
        repo.getSupportInfo().onSuccess { info ->
            if (info.telegramUrl.isNotBlank()) telegramUrl = info.telegramUrl
            else if (info.telegramUsername.isNotBlank()) {
                telegramUrl = "https://t.me/${info.telegramUsername.removePrefix("@")}"
            }
            if (info.email.isNotBlank()) supportEmail = info.email
            if (info.phone.isNotBlank()) {
                val fromApi = info.phone.split(',', ';', '|', '/')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                if (fromApi.isNotEmpty()) {
                    supportPhones = (AdminContact.PHONES + fromApi).distinct()
                }
            }
        }
    }

    fun openUri(uri: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        }
    }

    StickyScrollScreen(
        title = stringApp(R.string.profile_help),
        onBack = onBack,
    ) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringApp(R.string.help_contact_admin_title),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    AdminContact.OWNER_NAME,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringApp(R.string.help_contact_admin_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val email = supportEmail.ifBlank { AdminContact.EMAIL }
                Text(
                    email,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    "@${AdminContact.TELEGRAM_USERNAME}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                supportPhones.forEach { phone ->
                    Text(
                        phone,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { openUri(telegramUrl.ifBlank { AdminContact.TELEGRAM_URL }) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringApp(R.string.help_contact_telegram))
                    }
                    if (email.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val subject = Uri.encode(mailSubject)
                                val body = Uri.encode(
                                    "$mailBodyPrefix\n\n\n—\nUser: ${SessionManager.userEmail.orEmpty()}",
                                )
                                openUri("mailto:$email?subject=$subject&body=$body")
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringApp(R.string.help_contact_email))
                        }
                    }
                }
                supportPhones.forEach { phone ->
                    OutlinedButton(
                        onClick = { openUri("tel:${phone.filter { it.isDigit() || it == '+' }}") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("${stringApp(R.string.help_contact_phone)} · $phone")
                    }
                }
            }
        }

        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    stringApp(R.string.help_report_title),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringApp(R.string.help_report_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SUPPORT_TOPICS.forEach { option ->
                        FilterChip(
                            selected = topic == option,
                            onClick = { topic = option },
                            label = { Text(optionLabel(option)) },
                        )
                    }
                }
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it.take(1200)
                        formError = null
                        formSuccess = null
                    },
                    label = { Text(stringApp(R.string.help_report_message)) },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = replyEmail,
                    onValueChange = { replyEmail = it.take(120) },
                    label = { Text(stringApp(R.string.help_report_email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = replyPhone,
                    onValueChange = { replyPhone = it.take(40) },
                    label = { Text(stringApp(R.string.help_report_phone)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                formError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                formSuccess?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    enabled = !sending && Validation.requireSupportMessage(message) == null,
                    onClick = {
                        val err = Validation.requireSupportMessage(message)
                            ?: Validation.optionalEmail(replyEmail)
                            ?: Validation.optionalPhone(replyPhone)
                        if (err != null) {
                            formError = err
                            return@Button
                        }
                        scope.launch {
                            sending = true
                            formError = null
                            formSuccess = null
                            repo.sendMessage(
                                topic = topic,
                                message = message.trim(),
                                replyEmail = replyEmail.trim(),
                                replyPhone = replyPhone.trim(),
                            ).onSuccess {
                                formSuccess = reportSentMsg
                                message = ""
                            }.onFailure { formError = it.message }
                            sending = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        if (sending) "…" else stringApp(R.string.help_report_send),
                    )
                }
            }
        }

        Text(
            stringApp(R.string.help_faq_title),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
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
            stringApp(R.string.help_faq_subscribe_q),
            stringApp(R.string.help_faq_subscribe_a),
        )
        FaqCard(
            stringApp(R.string.help_faq_password_q),
            stringApp(R.string.help_faq_password_a),
        )
        FaqCard(
            stringApp(R.string.help_faq_contact_q),
            stringApp(R.string.help_faq_contact_a),
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
            Text(
                answer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
