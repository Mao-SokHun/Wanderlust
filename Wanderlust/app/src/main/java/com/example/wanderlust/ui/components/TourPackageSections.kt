package com.example.wanderlust.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.R
import com.example.wanderlust.data.model.TourPackageDetails
import com.example.wanderlust.data.model.durationLabel
import com.example.wanderlust.locale.optionLabel
import com.example.wanderlust.locale.stringApp

@Composable
fun TourPackageSections(pkg: TourPackageDetails, priceLabel: String = "") {
    val context = LocalContext.current

    if (priceLabel.isNotBlank() || pkg.durationLabel().isNotBlank() || pkg.departureDate.isNotBlank()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (priceLabel.isNotBlank()) {
                    Text(
                        stringApp(R.string.pkg_detail_price, priceLabel),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (pkg.durationLabel().isNotBlank()) {
                    Text(stringApp(R.string.pkg_detail_duration, pkg.durationLabel()))
                }
                if (pkg.departureDate.isNotBlank()) {
                    Text("${stringApp(R.string.pkg_departure)}: ${pkg.departureDate}")
                }
            }
        }
    }

    val includeLines = buildList {
        pkg.inclusions.transport.forEach { add(optionLabel(it)) }
        if (pkg.inclusions.accommodationType.isNotBlank()) {
            val stars = pkg.inclusions.accommodationStars.takeIf { it.isNotBlank() }?.let { " ($it★)" }.orEmpty()
            val occ = pkg.inclusions.occupancy.takeIf { it.isNotBlank() }?.let {
                " · ${stringApp(R.string.pkg_occupancy_per_room, it)}"
            }.orEmpty()
            add("${optionLabel(pkg.inclusions.accommodationType)}$stars$occ")
        }
        pkg.inclusions.meals.forEach { add(optionLabel(it)) }
        pkg.inclusions.activities.forEach { add(optionLabel(it)) }
        if (pkg.inclusions.insurance) add(stringApp(R.string.pkg_insurance))
        pkg.inclusions.custom.forEach { add(it) }
    }
    if (includeLines.isNotEmpty()) {
        PackageBulletCard(stringApp(R.string.pkg_detail_included), includeLines)
    }
    if (pkg.exclusions.isNotEmpty()) {
        PackageBulletCard(
            stringApp(R.string.pkg_detail_excluded),
            buildList { pkg.exclusions.forEach { add(optionLabel(it)) } },
        )
    }

    if (pkg.itinerary.any { day -> day.items.any { it.activity.isNotBlank() } }) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringApp(R.string.pkg_detail_itinerary), fontWeight = FontWeight.SemiBold)
                pkg.itinerary.forEach { day ->
                    val title = day.title.ifBlank { stringApp(R.string.pkg_day_n, day.day) }
                    Text(title, fontWeight = FontWeight.Medium)
                    day.items.filter { it.activity.isNotBlank() }.forEach { item ->
                        val line = buildString {
                            if (item.time.isNotBlank()) append("${item.time} — ")
                            append(item.activity)
                            if (item.location.isNotBlank()) append(" (${item.location})")
                        }
                        Text("• $line", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringApp(R.string.pkg_detail_terms), fontWeight = FontWeight.SemiBold)
            Text(stringApp(R.string.pkg_detail_deposit, pkg.booking.depositPercent))
            pkg.booking.cancellationRules.sortedByDescending { it.beforeDays }.forEach { rule ->
                if (rule.beforeDays <= 0) {
                    Text(stringApp(R.string.pkg_detail_cancel_last), style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(
                        stringApp(R.string.pkg_detail_cancel_rule, rule.beforeDays, rule.refundPercent),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            if (pkg.booking.whatToBring.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(stringApp(R.string.pkg_bring), fontWeight = FontWeight.Medium)
                pkg.booking.whatToBring.forEach {
                    Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    val contact = pkg.contact
    if (contact.phone.isNotBlank() || contact.telegram.isNotBlank() || contact.messenger.isNotBlank()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringApp(R.string.pkg_detail_contact), fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (contact.phone.isNotBlank()) {
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}")),
                                )
                            },
                        ) { Text(stringApp(R.string.pkg_contact_phone)) }
                    }
                    if (contact.telegram.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val raw = contact.telegram.trim()
                                val url = when {
                                    raw.startsWith("http") -> raw
                                    raw.startsWith("t.me/") -> "https://$raw"
                                    raw.startsWith("@") -> "https://t.me/${raw.removePrefix("@")}"
                                    else -> "https://t.me/$raw"
                                }
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                        ) { Text(stringApp(R.string.pkg_contact_telegram)) }
                    }
                }
                if (contact.messenger.isNotBlank()) {
                    Text(contact.messenger, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun PackageBulletCard(title: String, lines: List<String>) {
    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            lines.forEach {
                Text("• $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
