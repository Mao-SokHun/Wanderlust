package com.example.wanderlust.ui.screens.admin

import com.example.wanderlust.R

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.model.AdminPendingPayment
import com.example.wanderlust.data.repository.AdminRepository
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.StitchGhostCard
import kotlinx.coroutines.launch

@Composable
fun AdminPendingPaymentsScreen(onBack: () -> Unit) {
    val repo = remember { AdminRepository() }
    val scope = rememberCoroutineScope()
    val approvedMsg = stringApp(R.string.admin_payment_approved)
    val rejectedMsg = stringApp(R.string.admin_payment_rejected)
    var loading by remember { mutableStateOf(true) }
    var busyId by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var payments by remember { mutableStateOf<List<AdminPendingPayment>>(emptyList()) }

    fun reload() {
        scope.launch {
            loading = true
            error = null
            repo.getPendingPayments("pending")
                .onSuccess {
                    payments = it.payments
                    loading = false
                }
                .onFailure {
                    error = it.message
                    loading = false
                }
        }
    }

    LaunchedEffect(Unit) { reload() }

    StickyScrollScreen(
        title = stringApp(R.string.admin_payments_title),
        onBack = onBack,
    ) {
        Text(
            stringApp(R.string.admin_payments_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        if (loading) CircularProgressIndicator()
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        message?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
        }

        if (!loading && payments.isEmpty()) {
            Text(
                stringApp(R.string.admin_payments_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        payments.forEach { pay ->
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        pay.companyName.ifBlank { pay.userName }.ifBlank { "User #${pay.userId}" },
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(pay.userEmail, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Plan: ${pay.planId} · $${String.format("%.2f", pay.amountUsd)}")
                    Text("Bill: ${pay.billNumber}", style = MaterialTheme.typography.bodySmall)
                    if (pay.md5.isNotBlank()) {
                        Text("MD5: ${pay.md5.take(16)}…", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Button(
                            enabled = busyId == null,
                            onClick = {
                                scope.launch {
                                    busyId = pay.id
                                    error = null
                                    repo.approvePayment(pay.id)
                                        .onSuccess {
                                            message = it.message ?: approvedMsg
                                            busyId = null
                                            reload()
                                        }
                                        .onFailure {
                                            error = it.message
                                            busyId = null
                                        }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringApp(R.string.admin_payment_approve))
                        }
                        OutlinedButton(
                            enabled = busyId == null,
                            onClick = {
                                scope.launch {
                                    busyId = pay.id
                                    error = null
                                    repo.rejectPayment(pay.id)
                                        .onSuccess {
                                            message = it.message ?: rejectedMsg
                                            busyId = null
                                            reload()
                                        }
                                        .onFailure {
                                            error = it.message
                                            busyId = null
                                        }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringApp(R.string.admin_payment_reject))
                        }
                    }
                }
            }
        }
    }
}
