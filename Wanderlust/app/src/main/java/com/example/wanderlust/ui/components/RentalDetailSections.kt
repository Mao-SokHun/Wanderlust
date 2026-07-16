package com.example.wanderlust.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.wanderlust.data.model.RentalDetails
import com.example.wanderlust.locale.optionLabel
import com.example.wanderlust.locale.stringApp

@Composable
fun RentalDetailSections(rental: RentalDetails, priceLabel: String = "") {
    val context = LocalContext.current
    val modes = listOfNotNull(
        if (rental.withDriver) stringApp(R.string.rental_with_driver) else null,
        if (rental.selfDrive) stringApp(R.string.rental_self_drive) else null,
    )

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringApp(R.string.rental_detail_title), fontWeight = FontWeight.SemiBold)
            if (rental.makeModel.isNotBlank()) {
                Text(rental.makeModel, fontWeight = FontWeight.Medium)
            }
            if (priceLabel.isNotBlank()) Text(priceLabel)
            listOfNotNull(
                rental.vehicleType.takeIf { it.isNotBlank() }?.let { optionLabel(it) },
                rental.seats?.let { stringApp(R.string.detail_seats_label, it) },
                listOfNotNull(
                    rental.year.takeIf { it.isNotBlank() },
                    rental.color.takeIf { it.isNotBlank() },
                ).joinToString(" · ").takeIf { it.isNotBlank() },
            ).forEach {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
            if (rental.condition.isNotBlank()) {
                Text(rental.condition, style = MaterialTheme.typography.bodySmall)
            }
            if (!rental.available) {
                Text(
                    stringApp(R.string.rental_available_hint),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    if (modes.isNotEmpty()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.rental_detail_modes), fontWeight = FontWeight.SemiBold)
                modes.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }

    if (rental.destinationRates.isNotEmpty()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.rental_detail_dest), fontWeight = FontWeight.SemiBold)
                rental.destinationRates.forEach { rate ->
                    Text(
                        "${rate.from} → ${rate.to}: $${rate.priceUsd.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }

    if (rental.priceExclusions.isNotEmpty()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.rental_exclusions), fontWeight = FontWeight.SemiBold)
                rental.priceExclusions.forEach {
                    Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (
        rental.requiredDocuments.isNotEmpty() ||
        rental.securityDepositUsd != null ||
        rental.fuelPolicy.isNotBlank() ||
        rental.lateReturnFeeUsd != null
    ) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.rental_detail_req), fontWeight = FontWeight.SemiBold)
                rental.requiredDocuments.forEach {
                    Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                }
                rental.securityDepositUsd?.let {
                    Text(stringApp(R.string.rental_detail_deposit, it.toInt().toString()))
                }
                if (rental.fuelPolicy.isNotBlank()) {
                    Text(
                        "${stringApp(R.string.rental_fuel_policy)}: ${optionLabel(rental.fuelPolicy)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                rental.lateReturnFeeUsd?.let {
                    Text(
                        "${stringApp(R.string.rental_late_fee)}: $${it.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }

    if (rental.features.isNotEmpty() || rental.addOns.isNotEmpty()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (rental.features.isNotEmpty()) {
                    Text(stringApp(R.string.rental_features), fontWeight = FontWeight.SemiBold)
                    rental.features.forEach {
                        Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (rental.addOns.isNotEmpty()) {
                    Text(stringApp(R.string.rental_addons), fontWeight = FontWeight.SemiBold)
                    rental.addOns.forEach {
                        Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    val driver = rental.driver
    if (rental.withDriver && (driver.name.isNotBlank() || driver.languages.isNotEmpty())) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.rental_detail_driver), fontWeight = FontWeight.SemiBold)
                if (driver.name.isNotBlank()) Text(driver.name)
                if (driver.languages.isNotEmpty()) {
                    val langText = buildList {
                        driver.languages.forEach { add(optionLabel(it)) }
                    }.joinToString(", ")
                    Text(langText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    val contact = rental.contact
    if (contact.phone.isNotBlank() || contact.telegram.isNotBlank()) {
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
            }
        }
    }
}
