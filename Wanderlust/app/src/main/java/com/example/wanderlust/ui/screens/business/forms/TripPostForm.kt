package com.example.wanderlust.ui.screens.business.forms

import com.example.wanderlust.R

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.model.TourContactInfo
import com.example.wanderlust.data.model.TripDetails
import com.example.wanderlust.data.model.TripRecurring
import com.example.wanderlust.data.model.routeLabel
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.optionLabels
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.PostImagesField
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.util.Validation

private val CITIES = listOf(
    "Phnom Penh", "Siem Reap", "Sihanoukville", "Battambang", "Kampot", "Kep",
    "Koh Kong", "Kratie", "Mondulkiri", "Poipet", "Banlung",
)

private val TRIP_VEHICLES = listOf(
    "VIP Van / Minivan",
    "Seat Bus",
    "Sleeper Bus",
    "Sedan / SUV",
)

private val AMENITIES = listOf(
    "Free Wi-Fi",
    "USB Charging Ports",
    "Free Water & Wet Tissue",
    "Air Conditioning",
    "Restroom on Board",
    "Passenger Insurance",
)

private val SEAT_LAYOUTS = listOf(
    "ford_15" to "Ford / VIP van 15 seats",
    "bus_2_aisle" to "Seat bus 2-aisle",
    "sleeper_2deck" to "Sleeper bus 2-deck",
    "suv_7" to "SUV / shared 7 seats",
)

private val DEFAULT_SEATS = mapOf(
    "VIP Van / Minivan" to "15",
    "Seat Bus" to "40",
    "Sleeper Bus" to "32",
    "Sedan / SUV" to "7",
)

data class TripFormResult(
    val priceUsd: Double,
    val tripDetails: TripDetails,
)

@Composable
fun TripPostForm(
    posting: Boolean,
    onCancel: () -> Unit,
    onSubmit: (TripFormResult) -> Unit,
    initialPriceUsd: Double? = null,
    initialTrip: TripDetails? = null,
    isEditing: Boolean = false,
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 4
    val seed = initialTrip

    var departureCity by remember {
        mutableStateOf(seed?.departureCity?.takeIf { it.isNotBlank() } ?: "Phnom Penh")
    }
    var arrivalCity by remember {
        mutableStateOf(seed?.arrivalCity?.takeIf { it.isNotBlank() } ?: "Siem Reap")
    }
    var travelDate by remember { mutableStateOf(seed?.travelDate.orEmpty()) }
    var departureTime by remember {
        mutableStateOf(seed?.departureTime?.takeIf { it.isNotBlank() } ?: "07:00")
    }
    var arrivalTime by remember {
        mutableStateOf(seed?.arrivalTime?.takeIf { it.isNotBlank() } ?: "13:00")
    }
    var priceUsd by remember { mutableStateOf(initialPriceUsd?.toString().orEmpty()) }

    var vehicleType by remember {
        mutableStateOf(seed?.vehicleType?.takeIf { it.isNotBlank() } ?: "VIP Van / Minivan")
    }
    var amenities by remember {
        mutableStateOf(
            seed?.amenities?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Free Wi-Fi", "USB Charging Ports", "Air Conditioning", "Free Water & Wet Tissue"),
        )
    }
    var totalSeats by remember {
        mutableStateOf((seed?.totalSeats ?: 15).toString())
    }
    var seatLayout by remember {
        mutableStateOf(seed?.seatLayout?.takeIf { it.isNotBlank() } ?: "ford_15")
    }

    var boardingPoint by remember { mutableStateOf(seed?.boardingPoint.orEmpty()) }
    var boardingMapsUrl by remember { mutableStateOf(seed?.boardingMapsUrl.orEmpty()) }
    var dropOffPoint by remember { mutableStateOf(seed?.dropOffPoint.orEmpty()) }
    var dropOffNote by remember {
        mutableStateOf(seed?.dropOffNote?.takeIf { it.isNotBlank() } ?: "station")
    }
    var luggageKg by remember { mutableStateOf((seed?.luggageKg ?: 20).toString()) }
    var childPolicy by remember {
        mutableStateOf(
            seed?.childPolicy?.takeIf { it.isNotBlank() }
                ?: "Children under 5 or under 1m may sit on a parent's lap free of charge.",
        )
    }
    var cancelHours by remember { mutableStateOf((seed?.cancelHoursBefore ?: 24).toString()) }
    var cancelFee by remember { mutableStateOf((seed?.cancelFeePercent ?: 0).toString()) }
    var rescheduleHours by remember { mutableStateOf((seed?.rescheduleHoursBefore ?: 12).toString()) }

    var recurringEnabled by remember {
        // Editing a single trip instance — don't spawn new recurring rows
        mutableStateOf(if (isEditing) false else seed?.recurring?.enabled == true)
    }
    var recurringPattern by remember {
        mutableStateOf(seed?.recurring?.pattern?.takeIf { it.isNotBlank() } ?: "daily")
    }
    var untilDate by remember { mutableStateOf(seed?.recurring?.untilDate.orEmpty()) }
    var active by remember { mutableStateOf(seed?.active ?: true) }
    var phone by remember { mutableStateOf(seed?.contact?.phone.orEmpty()) }
    var telegram by remember { mutableStateOf(seed?.contact?.telegram.orEmpty()) }
    var imageUrls by remember { mutableStateOf(seed?.imageUrls.orEmpty()) }

    fun buildTrip(): TripDetails = TripDetails(
        departureCity = departureCity,
        arrivalCity = arrivalCity,
        travelDate = travelDate.trim(),
        departureTime = departureTime.trim(),
        arrivalTime = arrivalTime.trim(),
        vehicleType = vehicleType,
        amenities = amenities.toList(),
        totalSeats = totalSeats.toIntOrNull()?.coerceIn(1, 60) ?: 15,
        seatLayout = seatLayout,
        boardingPoint = boardingPoint.trim(),
        boardingMapsUrl = boardingMapsUrl.trim(),
        dropOffPoint = dropOffPoint.trim(),
        dropOffNote = dropOffNote,
        luggageKg = luggageKg.toIntOrNull()?.coerceIn(0, 100) ?: 20,
        childPolicy = childPolicy.trim(),
        cancelHoursBefore = cancelHours.toIntOrNull()?.coerceIn(0, 168) ?: 24,
        cancelFeePercent = cancelFee.toIntOrNull()?.coerceIn(0, 100) ?: 0,
        rescheduleHoursBefore = rescheduleHours.toIntOrNull()?.coerceIn(0, 168) ?: 12,
        recurring = TripRecurring(
            enabled = recurringEnabled,
            pattern = recurringPattern,
            untilDate = untilDate.trim(),
        ),
        active = active,
        contact = TourContactInfo(phone = phone.trim(), telegram = telegram.trim()),
        imageUrls = imageUrls,
    )

    fun stepErrorFor(s: Int): String? {
        return when (s) {
            0 -> Validation.requireDifferentCities(departureCity, arrivalCity)
                ?: Validation.requireIsoDate(
                    travelDate,
                    required = true,
                    notInPast = true,
                    labelEn = "Travel date",
                    labelKh = "ថ្ងៃធ្វើដំណើរ",
                )
                ?: Validation.requireTimeHm(
                    departureTime,
                    required = true,
                    labelEn = "Departure time",
                    labelKh = "ម៉ោងចេញ",
                )
                ?: Validation.requireTimeHm(
                    arrivalTime,
                    required = false,
                    labelEn = "Arrival time",
                    labelKh = "ម៉ោងដល់",
                )
                ?: Validation.requirePriceUsd(priceUsd)
            1 -> if (vehicleType.isBlank()) {
                if (AppLocale.isKhmer) "រើសប្រភេទយានយន្ត" else "Select a vehicle type"
            } else {
                null
            }
            2 -> Validation.requireSeats(totalSeats)
            else -> Validation.requireContactChannel(phone, telegram)
                ?: Validation.optionalUrl(boardingMapsUrl)
                ?: if (recurringEnabled) {
                    Validation.requireUntilAfterTravel(travelDate, untilDate)
                } else {
                    null
                }
        }
    }

    fun stepReady(): Boolean = stepErrorFor(step) == null
    val stepErrorText = stepErrorFor(step)

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringApp(R.string.trip_step_label, step + 1, totalSteps),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            LinearProgressIndicator(
                progress = { (step + 1f) / totalSteps },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                when (step) {
                    0 -> stringApp(R.string.trip_step_basics)
                    1 -> stringApp(R.string.trip_step_vehicle)
                    2 -> stringApp(R.string.trip_step_seats)
                    else -> stringApp(R.string.trip_step_policies)
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            when (step) {
                0 -> {
                    ChipPick(
                        label = stringApp(R.string.trip_departure),
                        options = CITIES,
                        selected = departureCity,
                        labels = optionLabels(CITIES),
                        onSelect = { departureCity = it },
                    )
                    ChipPick(
                        label = stringApp(R.string.trip_arrival),
                        options = CITIES,
                        selected = arrivalCity,
                        labels = optionLabels(CITIES),
                        onSelect = { arrivalCity = it },
                    )
                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { travelDate = it.filter { ch -> ch.isDigit() || ch == '-' }.take(10) },
                        label = { Text(stringApp(R.string.trip_travel_date)) },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = departureTime,
                            onValueChange = { departureTime = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5) },
                            label = { Text(stringApp(R.string.trip_depart_time)) },
                            placeholder = { Text("07:00") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = arrivalTime,
                            onValueChange = { arrivalTime = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5) },
                            label = { Text(stringApp(R.string.trip_arrive_time)) },
                            placeholder = { Text("13:00") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = priceUsd,
                        onValueChange = { priceUsd = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.trip_ticket_price)) },
                        placeholder = { Text("12") },
                        supportingText = { Text(stringApp(R.string.trip_price_per_seat)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                1 -> {
                    ChipPick(
                        label = stringApp(R.string.trip_vehicle_type),
                        options = TRIP_VEHICLES,
                        selected = vehicleType,
                        labels = optionLabels(TRIP_VEHICLES),
                        onSelect = {
                            vehicleType = it
                            totalSeats = DEFAULT_SEATS[it] ?: totalSeats
                            seatLayout = when (it) {
                                "VIP Van / Minivan" -> "ford_15"
                                "Sleeper Bus" -> "sleeper_2deck"
                                "Sedan / SUV" -> "suv_7"
                                else -> "bus_2_aisle"
                            }
                        },
                    )
                    Text(stringApp(R.string.trip_amenities), fontWeight = FontWeight.Medium)
                    ChipMulti(
                        options = AMENITIES,
                        selected = amenities,
                        labels = optionLabels(AMENITIES),
                        onToggle = { key ->
                            amenities = if (key in amenities) amenities - key else amenities + key
                        },
                    )
                }

                2 -> {
                    OutlinedTextField(
                        value = totalSeats,
                        onValueChange = { totalSeats = it.filter(Char::isDigit).take(2) },
                        label = { Text(stringApp(R.string.trip_total_seats)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    ChipPick(
                        label = stringApp(R.string.trip_seat_layout),
                        options = SEAT_LAYOUTS.map { it.first },
                        selected = seatLayout,
                        labels = optionLabels(SEAT_LAYOUTS.map { it.first }),
                        onSelect = { seatLayout = it },
                    )
                    Text(
                        stringApp(R.string.trip_seat_layout_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> {
                    OutlinedTextField(
                        value = boardingPoint,
                        onValueChange = { boardingPoint = it },
                        label = { Text(stringApp(R.string.trip_boarding)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                    OutlinedTextField(
                        value = boardingMapsUrl,
                        onValueChange = { boardingMapsUrl = it },
                        label = { Text(stringApp(R.string.trip_boarding_maps)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = dropOffPoint,
                        onValueChange = { dropOffPoint = it },
                        label = { Text(stringApp(R.string.trip_dropoff)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                    ChipPick(
                        label = stringApp(R.string.trip_dropoff_type),
                        options = listOf("station", "hotel"),
                        selected = dropOffNote,
                        labels = mapOf(
                            "station" to stringApp(R.string.trip_dropoff_station),
                            "hotel" to stringApp(R.string.trip_dropoff_hotel),
                        ),
                        onSelect = { dropOffNote = it },
                    )
                    OutlinedTextField(
                        value = luggageKg,
                        onValueChange = { luggageKg = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringApp(R.string.trip_luggage)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = childPolicy,
                        onValueChange = { childPolicy = it },
                        label = { Text(stringApp(R.string.trip_child_policy)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cancelHours,
                            onValueChange = { cancelHours = it.filter(Char::isDigit).take(3) },
                            label = { Text(stringApp(R.string.trip_cancel_hours)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = cancelFee,
                            onValueChange = { cancelFee = it.filter(Char::isDigit).take(3) },
                            label = { Text(stringApp(R.string.trip_cancel_fee)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = rescheduleHours,
                        onValueChange = { rescheduleHours = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringApp(R.string.trip_reschedule_hours)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(Modifier.height(4.dp))
                    Text(stringApp(R.string.trip_recurring), fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(stringApp(R.string.trip_repeat_daily), Modifier.weight(1f))
                        Switch(checked = recurringEnabled, onCheckedChange = { recurringEnabled = it })
                    }
                    if (recurringEnabled) {
                        ChipPick(
                            label = stringApp(R.string.trip_repeat_pattern),
                            options = listOf("daily", "weekdays"),
                            selected = recurringPattern,
                            labels = mapOf(
                                "daily" to stringApp(R.string.trip_pattern_daily),
                                "weekdays" to stringApp(R.string.trip_pattern_weekdays),
                            ),
                            onSelect = { recurringPattern = it },
                        )
                        OutlinedTextField(
                            value = untilDate,
                            onValueChange = { untilDate = it.take(32) },
                            label = { Text(stringApp(R.string.trip_until_date)) },
                            placeholder = { Text("YYYY-MM-DD") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(stringApp(R.string.trip_active), fontWeight = FontWeight.Medium)
                            Text(
                                stringApp(R.string.trip_active_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = active, onCheckedChange = { active = it })
                    }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.take(40) },
                        label = { Text(stringApp(R.string.pkg_contact_phone)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = telegram,
                        onValueChange = { telegram = it.take(120) },
                        label = { Text(stringApp(R.string.pkg_contact_telegram)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    PostImagesField(
                        imageUrls = imageUrls,
                        onImageUrlsChange = { imageUrls = it },
                    )
                }
            }

            stepErrorText?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (step == 0) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                        Text(stringApp(R.string.business_cancel_post))
                    }
                } else {
                    OutlinedButton(onClick = { step -= 1 }, modifier = Modifier.weight(1f)) {
                        Text(stringApp(R.string.pkg_back))
                    }
                }
                Button(
                    enabled = !posting && stepReady(),
                    onClick = {
                        if (step < totalSteps - 1) {
                            step += 1
                        } else {
                            val usd = priceUsd.toDoubleOrNull() ?: return@Button
                            onSubmit(TripFormResult(priceUsd = usd, tripDetails = buildTrip()))
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        when {
                            posting -> "…"
                            step < totalSteps - 1 -> stringApp(R.string.pkg_next)
                            else -> stringApp(
                                if (isEditing) R.string.business_save_changes else R.string.trip_publish,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipPick(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    labels: Map<String, String> = emptyMap(),
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(labels[option] ?: option) },
                )
            }
        }
    }
}

@Composable
private fun ChipMulti(
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    labels: Map<String, String> = emptyMap(),
) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option in selected,
                onClick = { onToggle(option) },
                label = { Text(labels[option] ?: option) },
            )
        }
    }
}
