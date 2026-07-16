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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.model.AdminBillingPlanUpdateRequest
import com.example.wanderlust.data.model.AdminBillingSettingsRequest
import com.example.wanderlust.data.model.BillingPlan
import com.example.wanderlust.data.repository.AdminRepository
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.StitchGhostCard
import kotlinx.coroutines.launch

@Composable
fun AdminBillingPlansScreen(onBack: () -> Unit) {
    val repo = remember { AdminRepository() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringApp(R.string.admin_billing_saved)
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var plans by remember { mutableStateOf<List<BillingPlan>>(emptyList()) }
    var rateText by remember { mutableStateOf("4100") }
    val priceDrafts = remember { mutableStateMapOf<String, String>() }
    val nameDrafts = remember { mutableStateMapOf<String, String>() }
    val activeDrafts = remember { mutableStateMapOf<String, Boolean>() }

    fun reload() {
        scope.launch {
            loading = true
            error = null
            repo.getBillingPlans()
                .onSuccess { res ->
                    plans = res.plans
                    rateText = res.usdToKhrRate.toInt().toString()
                    priceDrafts.clear()
                    nameDrafts.clear()
                    activeDrafts.clear()
                    res.plans.forEach { p ->
                        priceDrafts[p.id] = String.format("%.2f", p.priceUsd)
                        nameDrafts[p.id] = p.name
                        activeDrafts[p.id] = p.active
                    }
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
        title = stringApp(R.string.admin_billing_plans_title),
        onBack = onBack,
    ) {
        Text(
            stringApp(R.string.admin_billing_plans_hint),
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

        if (!loading) {
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        stringApp(R.string.admin_usd_khr_rate),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it.filter { ch -> ch.isDigit() }.take(5) },
                        label = { Text("1 USD = ? ៛") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        enabled = !saving,
                        onClick = {
                            val rate = rateText.toDoubleOrNull()
                            if (rate == null) {
                                error = "Invalid rate"
                                return@Button
                            }
                            scope.launch {
                                saving = true
                                error = null
                                repo.updateBillingSettings(AdminBillingSettingsRequest(usdToKhrRate = rate))
                                    .onSuccess {
                                        rateText = it.usdToKhrRate.toInt().toString()
                                        message = savedMsg
                                        saving = false
                                        reload()
                                    }
                                    .onFailure {
                                        error = it.message
                                        saving = false
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringApp(R.string.admin_save_rate))
                    }
                }
            }

            plans.forEach { plan ->
                StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(plan.id, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nameDrafts[plan.id] ?: plan.name,
                            onValueChange = { nameDrafts[plan.id] = it.take(80) },
                            label = { Text(stringApp(R.string.admin_plan_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = priceDrafts[plan.id] ?: String.format("%.2f", plan.priceUsd),
                            onValueChange = { v ->
                                priceDrafts[plan.id] = v.filter { it.isDigit() || it == '.' }.take(10)
                            },
                            label = { Text(stringApp(R.string.admin_plan_price_usd)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                val price = priceDrafts[plan.id]?.toDoubleOrNull() ?: plan.priceUsd
                                val rate = rateText.toDoubleOrNull() ?: 4100.0
                                val khr = (price * rate).toInt().coerceAtLeast(1)
                                Text("≈ %,d ៛".format(khr))
                            },
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringApp(R.string.admin_plan_active))
                            Switch(
                                checked = activeDrafts[plan.id] ?: plan.active,
                                onCheckedChange = { activeDrafts[plan.id] = it },
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            enabled = !saving,
                            onClick = {
                                val price = priceDrafts[plan.id]?.toDoubleOrNull()
                                if (price == null || price < 0.01) {
                                    error = "Price must be ≥ 0.01"
                                    return@Button
                                }
                                scope.launch {
                                    saving = true
                                    error = null
                                    repo.updateBillingPlan(
                                        plan.id,
                                        AdminBillingPlanUpdateRequest(
                                            priceUsd = price,
                                            name = nameDrafts[plan.id],
                                            active = activeDrafts[plan.id],
                                        ),
                                    ).onSuccess {
                                        message = savedMsg
                                        saving = false
                                        reload()
                                    }.onFailure {
                                        error = it.message
                                        saving = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                if (saving) "…" else stringApp(R.string.admin_save_plan),
                            )
                        }
                    }
                }
            }
        }
    }
}
