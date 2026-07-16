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
import com.example.wanderlust.data.model.RentalDestinationRate
import com.example.wanderlust.data.model.RentalDetails
import com.example.wanderlust.data.model.RentalDriverProfile
import com.example.wanderlust.data.model.TourContactInfo
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.optionLabels
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.PostImagesField
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.util.Validation

private val RENTAL_TYPES = listOf(
    "SUV / Crossover (5 seats)",
    "Minivan / MPV (7–9 seats)",
    "VIP Van (12–15 seats)",
)

private val DEFAULT_SEATS = mapOf(
    "SUV / Crossover (5 seats)" to "5",
    "Minivan / MPV (7–9 seats)" to "7",
    "VIP Van (12–15 seats)" to "12",
)

private val PRICE_EXCLUSIONS = listOf(
    "Fuel (guest pays)",
    "Expressway tolls",
    "Driver meals & lodging (multi-day)",
)

private val DOCS = listOf(
    "National ID (copy or original)",
    "Valid driving license",
    "Security deposit",
)

private val FEATURES = listOf(
    "Dash camera",
    "Bluetooth / Apple CarPlay",
    "Stability control",
    "Strong A/C",
    "Spacious cabin",
)

private val ADDONS = listOf(
    "Baby car seat",
    "Roof rack",
)

private val LANGS = listOf("Khmer", "English", "Chinese")

private val CITIES = listOf(
    "Phnom Penh", "Siem Reap", "Sihanoukville", "Battambang", "Kampot", "Kep",
)

data class RentalFormResult(
    val priceUsd: Double,
    val rentalDetails: RentalDetails,
    val serviceArea: String,
)

@Composable
fun RentalPostForm(
    posting: Boolean,
    onCancel: () -> Unit,
    onSubmit: (RentalFormResult) -> Unit,
    initialServiceArea: String = "Phnom Penh",
    initialDetails: RentalDetails? = null,
    isEditing: Boolean = false,
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 4
    val seed = initialDetails

    var makeModel by remember { mutableStateOf(seed?.makeModel.orEmpty()) }
    var vehicleType by remember {
        mutableStateOf(seed?.vehicleType?.takeIf { it.isNotBlank() } ?: RENTAL_TYPES[0])
    }
    var year by remember { mutableStateOf(seed?.year.orEmpty()) }
    var color by remember { mutableStateOf(seed?.color.orEmpty()) }
    var condition by remember {
        mutableStateOf(
            seed?.condition?.takeIf { it.isNotBlank() }
                ?: "Clean car, strong A/C, spacious cabin",
        )
    }
    var seats by remember {
        mutableStateOf((seed?.seats ?: DEFAULT_SEATS[RENTAL_TYPES[0]]?.toIntOrNull() ?: 5).toString())
    }
    var serviceArea by remember {
        mutableStateOf(initialServiceArea.ifBlank { "Phnom Penh" })
    }

    var withDriver by remember { mutableStateOf(seed?.withDriver ?: true) }
    var selfDrive by remember { mutableStateOf(seed?.selfDrive ?: false) }

    var pricePerDay by remember {
        mutableStateOf(seed?.pricePerDay?.toString().orEmpty())
    }
    var exclusions by remember {
        mutableStateOf(
            seed?.priceExclusions?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Fuel (guest pays)", "Expressway tolls"),
        )
    }
    var destFrom by remember { mutableStateOf("Phnom Penh") }
    var destTo by remember { mutableStateOf("Siem Reap") }
    var destPrice by remember { mutableStateOf("") }
    var destinationRates by remember { mutableStateOf(seed?.destinationRates.orEmpty()) }

    var docs by remember {
        mutableStateOf(
            seed?.requiredDocuments?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("National ID (copy or original)", "Valid driving license", "Security deposit"),
        )
    }
    var deposit by remember { mutableStateOf(seed?.securityDepositUsd?.toString().orEmpty()) }
    var fuelPolicy by remember {
        mutableStateOf(seed?.fuelPolicy?.takeIf { it.isNotBlank() } ?: "Full to Full")
    }
    var lateFee by remember { mutableStateOf(seed?.lateReturnFeeUsd?.toString().orEmpty()) }

    var features by remember {
        mutableStateOf(
            seed?.features?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Dash camera", "Bluetooth / Apple CarPlay", "Strong A/C"),
        )
    }
    var addOns by remember { mutableStateOf(seed?.addOns?.toSet().orEmpty()) }
    var available by remember { mutableStateOf(seed?.available ?: true) }
    var driverName by remember { mutableStateOf(seed?.driver?.name.orEmpty()) }
    var driverLangs by remember {
        mutableStateOf(seed?.driver?.languages?.toSet()?.takeIf { it.isNotEmpty() } ?: setOf("Khmer"))
    }
    var phone by remember { mutableStateOf(seed?.contact?.phone.orEmpty()) }
    var telegram by remember { mutableStateOf(seed?.contact?.telegram.orEmpty()) }
    var imageUrls by remember { mutableStateOf(seed?.imageUrls.orEmpty()) }

    fun build(): RentalDetails = RentalDetails(
        makeModel = makeModel.trim(),
        vehicleType = vehicleType,
        year = year.trim(),
        color = color.trim(),
        condition = condition.trim(),
        seats = seats.toIntOrNull(),
        withDriver = withDriver,
        selfDrive = selfDrive,
        pricePerDay = pricePerDay.toDoubleOrNull(),
        destinationRates = destinationRates,
        priceExclusions = exclusions.toList(),
        requiredDocuments = docs.toList(),
        securityDepositUsd = deposit.toDoubleOrNull(),
        fuelPolicy = fuelPolicy.trim().ifBlank { "Full to Full" },
        lateReturnFeeUsd = lateFee.toDoubleOrNull(),
        features = features.toList(),
        addOns = addOns.toList(),
        available = available,
        driver = RentalDriverProfile(
            name = driverName.trim(),
            languages = driverLangs.toList(),
        ),
        contact = TourContactInfo(phone = phone.trim(), telegram = telegram.trim()),
        imageUrls = imageUrls,
    )

    fun stepErrorFor(s: Int): String? {
        return when (s) {
            0 -> Validation.requireMakeModel(makeModel)
                ?: if (vehicleType.isBlank()) {
                    if (AppLocale.isKhmer) "រើសប្រភេទរថយន្ត" else "Select a vehicle type"
                } else {
                    null
                }
                ?: Validation.optionalSeats(seats)
                ?: if (condition.length > Validation.CONDITION_MAX) {
                    if (AppLocale.isKhmer) "ស្ថានភាពវែងពេក" else "Condition is too long"
                } else {
                    null
                }
            1 -> when {
                !withDriver && !selfDrive ->
                    if (AppLocale.isKhmer) {
                        "រើសមានតៃកុង និង/ឬ បើកខ្លួនឯង"
                    } else {
                        "Select With Driver and/or Self-Drive"
                    }
                else -> Validation.requirePriceUsd(pricePerDay)
            }
            2 -> Validation.optionalPriceUsd(deposit)
                ?: Validation.optionalPriceUsd(lateFee)
            else -> Validation.requireContactChannel(phone, telegram)
                ?: run {
                    for (url in imageUrls) {
                        Validation.optionalUrl(url)?.let { return it }
                    }
                    null
                }
                ?: if (withDriver && driverName.trim().isEmpty()) {
                    if (AppLocale.isKhmer) "បញ្ចូលឈ្មោះតៃកុង" else "Enter driver name"
                } else {
                    null
                }
        }
    }

    fun stepReady(): Boolean = stepErrorFor(step) == null
    val stepError = stepErrorFor(step)

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringApp(R.string.rental_step_label, step + 1, totalSteps),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            LinearProgressIndicator(
                progress = { (step + 1f) / totalSteps },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                when (step) {
                    0 -> stringApp(R.string.rental_step_vehicle)
                    1 -> stringApp(R.string.rental_step_pricing)
                    2 -> stringApp(R.string.rental_step_requirements)
                    else -> stringApp(R.string.rental_step_features)
                },
                fontWeight = FontWeight.Bold,
            )

            when (step) {
                0 -> {
                    OutlinedTextField(
                        value = makeModel,
                        onValueChange = { makeModel = it.take(Validation.MAKE_MODEL_MAX) },
                        label = { Text(stringApp(R.string.rental_make_model)) },
                        placeholder = { Text("Toyota Alphard / Hyundai H-1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    ChipRow(RENTAL_TYPES, setOf(vehicleType), single = true, labels = optionLabels(RENTAL_TYPES)) {
                        vehicleType = it
                        seats = DEFAULT_SEATS[it] ?: seats
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it.filter(Char::isDigit).take(4) },
                            label = { Text(stringApp(R.string.rental_year)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it.take(40) },
                            label = { Text(stringApp(R.string.rental_color)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = seats,
                        onValueChange = { seats = it.filter(Char::isDigit).take(2) },
                        label = { Text(stringApp(R.string.rental_seats)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        label = { Text(stringApp(R.string.rental_condition)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                    ChipRow(CITIES, setOf(serviceArea), single = true, labels = optionLabels(CITIES)) {
                        serviceArea = it
                    }
                }

                1 -> {
                    Text(stringApp(R.string.rental_options), fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringApp(R.string.rental_with_driver), Modifier.weight(1f))
                        Switch(checked = withDriver, onCheckedChange = { withDriver = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringApp(R.string.rental_self_drive), Modifier.weight(1f))
                        Switch(checked = selfDrive, onCheckedChange = { selfDrive = it })
                    }
                    OutlinedTextField(
                        value = pricePerDay,
                        onValueChange = { pricePerDay = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.rental_price_day)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Text(stringApp(R.string.rental_exclusions), fontWeight = FontWeight.Medium)
                    ChipRow(PRICE_EXCLUSIONS, exclusions, labels = optionLabels(PRICE_EXCLUSIONS)) {
                        exclusions = if (it in exclusions) exclusions - it else exclusions + it
                    }
                    if (withDriver) {
                        Text(stringApp(R.string.rental_dest_rates), fontWeight = FontWeight.Medium)
                        ChipRow(CITIES, setOf(destFrom), single = true, labels = optionLabels(CITIES)) {
                            destFrom = it
                        }
                        ChipRow(CITIES, setOf(destTo), single = true, labels = optionLabels(CITIES)) {
                            destTo = it
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = destPrice,
                                onValueChange = { destPrice = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("USD") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                            )
                            TextButtonLike {
                                if (Validation.requireDifferentCities(destFrom, destTo) == null &&
                                    Validation.requirePriceUsd(destPrice) == null
                                ) {
                                    val p = destPrice.toDoubleOrNull() ?: return@TextButtonLike
                                    destinationRates = destinationRates + RentalDestinationRate(destFrom, destTo, p)
                                    destPrice = ""
                                }
                            }
                        }
                        if (destinationRates.isNotEmpty()) {
                            Text(
                                destinationRates.joinToString("\n") { "${it.from} → ${it.to}: $${it.priceUsd}" },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                2 -> {
                    Text(stringApp(R.string.rental_documents), fontWeight = FontWeight.Medium)
                    ChipRow(DOCS, docs, labels = optionLabels(DOCS)) {
                        docs = if (it in docs) docs - it else docs + it
                    }
                    OutlinedTextField(
                        value = deposit,
                        onValueChange = { deposit = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.rental_deposit)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = fuelPolicy,
                        onValueChange = { fuelPolicy = it },
                        label = { Text(stringApp(R.string.rental_fuel_policy)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = lateFee,
                        onValueChange = { lateFee = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.rental_late_fee)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                else -> {
                    Text(stringApp(R.string.rental_features), fontWeight = FontWeight.Medium)
                    ChipRow(FEATURES, features, labels = optionLabels(FEATURES)) {
                        features = if (it in features) features - it else features + it
                    }
                    Text(stringApp(R.string.rental_addons), fontWeight = FontWeight.Medium)
                    ChipRow(ADDONS, addOns, labels = optionLabels(ADDONS)) {
                        addOns = if (it in addOns) addOns - it else addOns + it
                    }
                    if (withDriver) {
                        OutlinedTextField(
                            value = driverName,
                            onValueChange = { driverName = it },
                            label = { Text(stringApp(R.string.rental_driver_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        ChipRow(LANGS, driverLangs, labels = optionLabels(LANGS)) {
                            driverLangs = if (it in driverLangs) driverLangs - it else driverLangs + it
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(stringApp(R.string.rental_available), fontWeight = FontWeight.Medium)
                            Text(
                                stringApp(R.string.rental_available_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = available, onCheckedChange = { available = it })
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

            stepError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        if (step < totalSteps - 1) step += 1
                        else {
                            val usd = pricePerDay.toDoubleOrNull() ?: return@Button
                            onSubmit(
                                RentalFormResult(
                                    priceUsd = usd,
                                    rentalDetails = build(),
                                    serviceArea = serviceArea,
                                ),
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        when {
                            posting -> "…"
                            step < totalSteps - 1 -> stringApp(R.string.pkg_next)
                            else -> stringApp(
                                if (isEditing) R.string.business_save_changes else R.string.rental_publish,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TextButtonLike(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(top = 8.dp)) {
        Text(stringApp(R.string.pkg_add))
    }
}

@Composable
private fun ChipRow(
    options: List<String>,
    selected: Set<String>,
    single: Boolean = false,
    labels: Map<String, String> = emptyMap(),
    onToggle: (String) -> Unit,
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
    Spacer(Modifier.height(4.dp))
}
