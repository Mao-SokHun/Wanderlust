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
import com.example.wanderlust.data.model.TripDetails
import com.example.wanderlust.data.model.routeLabel
import com.example.wanderlust.data.model.scheduleLabel
import com.example.wanderlust.locale.optionLabel
import com.example.wanderlust.locale.stringApp

@Composable
fun TripDetailSections(trip: TripDetails, priceLabel: String = "") {
    val context = LocalContext.current
    val layoutLabel = optionLabel(trip.seatLayout).let {
        if (it == trip.seatLayout) {
            when (trip.seatLayout) {
                "ford_15" -> stringApp(R.string.opt_layout_ford)
                "sleeper_2deck" -> stringApp(R.string.opt_layout_sleeper)
                "suv_7" -> stringApp(R.string.opt_layout_suv)
                else -> stringApp(R.string.opt_layout_bus)
            }
        } else it
    }

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringApp(R.string.trip_detail_route), fontWeight = FontWeight.SemiBold)
            Text(trip.routeLabel())
            if (priceLabel.isNotBlank()) Text(priceLabel, fontWeight = FontWeight.Medium)
            Text(
                "${stringApp(R.string.trip_detail_schedule)}: ${trip.scheduleLabel()}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                stringApp(R.string.trip_detail_seats, trip.totalSeats, layoutLabel),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(optionLabel(trip.vehicleType), style = MaterialTheme.typography.bodySmall)
        }
    }

    if (trip.amenities.isNotEmpty()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringApp(R.string.trip_detail_amenities), fontWeight = FontWeight.SemiBold)
                trip.amenities.forEach {
                    Text("• ${optionLabel(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (trip.boardingPoint.isNotBlank() || trip.dropOffPoint.isNotBlank()) {
        StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (trip.boardingPoint.isNotBlank()) {
                    Text(stringApp(R.string.trip_boarding), fontWeight = FontWeight.Medium)
                    Text(trip.boardingPoint, style = MaterialTheme.typography.bodySmall)
                }
                if (trip.dropOffPoint.isNotBlank()) {
                    Text(stringApp(R.string.trip_dropoff), fontWeight = FontWeight.Medium)
                    Text(trip.dropOffPoint, style = MaterialTheme.typography.bodySmall)
                }
                if (trip.boardingMapsUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(trip.boardingMapsUrl)),
                            )
                        },
                    ) {
                        Text(stringApp(R.string.trip_boarding_maps))
                    }
                }
            }
        }
    }

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringApp(R.string.trip_detail_policies), fontWeight = FontWeight.SemiBold)
            Text(stringApp(R.string.trip_detail_luggage, trip.luggageKg))
            if (trip.childPolicy.isNotBlank()) {
                Text(trip.childPolicy, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                stringApp(R.string.trip_detail_cancel, trip.cancelHoursBefore, trip.cancelFeePercent),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    val contact = trip.contact
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
