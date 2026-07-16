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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.model.TourBookingTerms
import com.example.wanderlust.data.model.TourCancelRule
import com.example.wanderlust.data.model.TourContactInfo
import com.example.wanderlust.data.model.TourInclusions
import com.example.wanderlust.data.model.TourItineraryDay
import com.example.wanderlust.data.model.TourItineraryItem
import com.example.wanderlust.data.model.TourPackageDetails
import com.example.wanderlust.data.model.durationLabel
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.optionLabels
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.PostImagesField
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.util.Validation

private val TOUR_TYPES = listOf(
    "Adventure", "Relax", "Camping", "Culture", "History", "Temple", "Beach", "Nature", "Food", "City",
)

private val TRANSPORT_OPTIONS = listOf(
    "VIP tour van", "Bus", "Private car", "Pick-up & drop-off",
)

private val ACCOMMODATION_TYPES = listOf("Hotel", "Resort", "Camping", "Homestay")
private val STAR_OPTIONS = listOf("1", "2", "3", "4", "5")
private val OCCUPANCY_OPTIONS = listOf("2", "3", "4")
private val MEAL_OPTIONS = listOf(
    "Breakfast", "Lunch", "Dinner", "Drinking water", "Cold towels",
)
private val ACTIVITY_OPTIONS = listOf(
    "Entrance tickets", "Boat ticket", "Protected area ticket", "Local tour guide",
)
private val DEFAULT_EXCLUSIONS = listOf(
    "Personal expenses",
    "Meals not listed in the program",
    "Tips for driver / guide",
)
private val BRING_OPTIONS = listOf(
    "Long pants for temples / palace",
    "Hiking shoes",
    "Personal medicine",
    "Sunscreen & hat",
    "Light jacket",
)
private val DEPOSIT_OPTIONS = listOf("30", "50", "100")
private val CITY_OPTIONS = listOf(
    "Phnom Penh", "Siem Reap", "Sihanoukville", "Battambang", "Kampot", "Kep",
    "Koh Kong", "Kratie", "Mondulkiri",
)

data class TourPackageFormResult(
    val title: String,
    val description: String,
    val location: String,
    val priceUsd: Double,
    val priceLabel: String,
    val duration: String,
    val category: String,
    val packageDetails: TourPackageDetails,
)

@Composable
fun TourPackagePostForm(
    posting: Boolean,
    onCancel: () -> Unit,
    onSubmit: (TourPackageFormResult) -> Unit,
    initialTitle: String = "",
    initialDescription: String = "",
    initialLocation: String = "",
    initialPriceUsd: Double? = null,
    initialPriceLabel: String = "",
    initialDetails: TourPackageDetails? = null,
    isEditing: Boolean = false,
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 4
    val seed = initialDetails

    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var tourType by remember { mutableStateOf(seed?.tourType?.takeIf { it.isNotBlank() } ?: "Adventure") }
    var location by remember { mutableStateOf(initialLocation.ifBlank { "Phnom Penh" }) }
    var days by remember { mutableStateOf((seed?.days ?: 3).toString()) }
    var nights by remember { mutableStateOf((seed?.nights ?: 2).toString()) }
    var departureDate by remember { mutableStateOf(seed?.departureDate.orEmpty()) }
    var priceUsd by remember { mutableStateOf(initialPriceUsd?.toString().orEmpty()) }
    var priceType by remember { mutableStateOf(seed?.priceType?.takeIf { it.isNotBlank() } ?: "per_person") }
    var priceLabel by remember { mutableStateOf(initialPriceLabel) }

    var transport by remember {
        mutableStateOf(
            seed?.inclusions?.transport?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("VIP tour van", "Pick-up & drop-off"),
        )
    }
    var accommodationType by remember {
        mutableStateOf(seed?.inclusions?.accommodationType?.takeIf { it.isNotBlank() } ?: "Hotel")
    }
    var accommodationStars by remember {
        mutableStateOf(seed?.inclusions?.accommodationStars?.takeIf { it.isNotBlank() } ?: "3")
    }
    var occupancy by remember {
        mutableStateOf(seed?.inclusions?.occupancy?.takeIf { it.isNotBlank() } ?: "2")
    }
    var meals by remember {
        mutableStateOf(
            seed?.inclusions?.meals?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Breakfast", "Lunch", "Dinner", "Drinking water"),
        )
    }
    var activities by remember {
        mutableStateOf(
            seed?.inclusions?.activities?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Entrance tickets", "Local tour guide"),
        )
    }
    var insurance by remember { mutableStateOf(seed?.inclusions?.insurance ?: true) }
    var customInclusion by remember { mutableStateOf("") }
    var customInclusions by remember { mutableStateOf(seed?.inclusions?.custom.orEmpty()) }
    var exclusions by remember {
        mutableStateOf(
            seed?.exclusions?.toSet()?.takeIf { it.isNotEmpty() } ?: DEFAULT_EXCLUSIONS.toSet(),
        )
    }
    var customExclusion by remember { mutableStateOf("") }

    var itinerary by remember {
        mutableStateOf(
            seed?.itinerary?.takeIf { it.isNotEmpty() }
                ?: listOf(
                    TourItineraryDay(
                        day = 1,
                        title = "Day 1",
                        items = listOf(TourItineraryItem(time = "06:00", activity = "", location = "")),
                    ),
                ),
        )
    }

    var depositPercent by remember {
        mutableStateOf((seed?.booking?.depositPercent ?: 50).toString())
    }
    var whatToBring by remember {
        mutableStateOf(
            seed?.booking?.whatToBring?.toSet()?.takeIf { it.isNotEmpty() }
                ?: setOf("Long pants for temples / palace", "Personal medicine"),
        )
    }
    var customBring by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(seed?.contact?.phone.orEmpty()) }
    var telegram by remember { mutableStateOf(seed?.contact?.telegram.orEmpty()) }
    var messenger by remember { mutableStateOf(seed?.contact?.messenger.orEmpty()) }
    var imageUrls by remember { mutableStateOf(seed?.imageUrls.orEmpty()) }

    fun buildPackage(): TourPackageDetails {
        val d = days.toIntOrNull()?.coerceIn(0, 21) ?: 1
        val n = nights.toIntOrNull()?.coerceIn(0, 21) ?: 0
        return TourPackageDetails(
            tourType = tourType,
            days = d,
            nights = n,
            departureDate = departureDate.trim(),
            priceType = priceType,
            inclusions = TourInclusions(
                transport = transport.toList(),
                accommodationType = accommodationType,
                accommodationStars = accommodationStars,
                occupancy = occupancy,
                meals = meals.toList(),
                activities = activities.toList(),
                insurance = insurance,
                custom = customInclusions,
            ),
            exclusions = exclusions.toList(),
            itinerary = itinerary.mapIndexed { index, day ->
                day.copy(day = index + 1)
            },
            booking = TourBookingTerms(
                depositPercent = depositPercent.toIntOrNull()?.coerceIn(0, 100) ?: 50,
                cancellationRules = listOf(
                    TourCancelRule(7, 100),
                    TourCancelRule(3, 50),
                    TourCancelRule(0, 0),
                ),
                whatToBring = whatToBring.toList(),
            ),
            contact = TourContactInfo(
                phone = phone.trim(),
                telegram = telegram.trim(),
                messenger = messenger.trim(),
            ),
            imageUrls = imageUrls,
        )
    }

    fun stepErrorFor(s: Int): String? {
        return when (s) {
            0 -> Validation.requireTourTitle(title)
                ?: Validation.requireTourDescription(description)
                ?: Validation.requirePriceUsd(priceUsd)
                ?: Validation.requireLocation(location)
                ?: Validation.requireDays(days)
                ?: Validation.optionalNights(nights)
                ?: Validation.requireIsoDate(
                    departureDate,
                    required = true,
                    notInPast = true,
                    labelEn = "Departure date",
                    labelKh = "ថ្ងៃចេញដំណើរ",
                )
            1 -> null
            2 -> {
                if (!itinerary.any { day -> day.items.any { it.activity.isNotBlank() } }) {
                    return if (AppLocale.isKhmer) {
                        "បញ្ចូលសកម្មភាពយ៉ាងហោចមួយក្នុងកម្មវិធី"
                    } else {
                        "Add at least one itinerary activity"
                    }
                }
                for (day in itinerary) {
                    for (item in day.items) {
                        if (item.activity.trim().length > Validation.ACTIVITY_MAX) {
                            return if (AppLocale.isKhmer) {
                                "សកម្មភាពវែងពេក"
                            } else {
                                "Activity text is too long"
                            }
                        }
                        Validation.requireTimeHm(item.time, required = false)?.let { return it }
                    }
                }
                null
            }
            else -> {
                Validation.requireContactChannel(phone, telegram, messenger)
                    ?: run {
                        for (url in imageUrls) {
                            Validation.optionalUrl(url)?.let { return it }
                        }
                        null
                    }
            }
        }
    }

    fun stepReady(): Boolean = stepErrorFor(step) == null
    val stepErrorText = stepErrorFor(step)

    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                stringApp(R.string.pkg_step_label, step + 1, totalSteps),
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
                    0 -> stringApp(R.string.pkg_step_basics)
                    1 -> stringApp(R.string.pkg_step_includes)
                    2 -> stringApp(R.string.pkg_step_itinerary)
                    else -> stringApp(R.string.pkg_step_terms)
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            when (step) {
                0 -> {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.take(Validation.TITLE_MAX) },
                        label = { Text(stringApp(R.string.label_tour_title)) },
                        placeholder = { Text(stringApp(R.string.hint_tour_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.take(Validation.DESCRIPTION_MAX) },
                        label = { Text(stringApp(R.string.label_description)) },
                        placeholder = { Text(stringApp(R.string.hint_tour_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_tour_type),
                        options = TOUR_TYPES,
                        selected = setOf(tourType),
                        singleSelect = true,
                        labels = optionLabels(TOUR_TYPES),
                        onToggle = { tourType = it },
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.label_pick_city),
                        options = CITY_OPTIONS,
                        selected = setOf(location),
                        singleSelect = true,
                        labels = optionLabels(CITY_OPTIONS),
                        onToggle = { location = it },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = days,
                            onValueChange = { days = it.filter(Char::isDigit).take(2) },
                            label = { Text(stringApp(R.string.pkg_days)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = nights,
                            onValueChange = { nights = it.filter(Char::isDigit).take(2) },
                            label = { Text(stringApp(R.string.pkg_nights)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = departureDate,
                        onValueChange = { departureDate = it.filter { ch -> ch.isDigit() || ch == '-' }.take(10) },
                        label = { Text(stringApp(R.string.pkg_departure)) },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_price_type),
                        options = listOf("per_person", "group"),
                        selected = setOf(priceType),
                        singleSelect = true,
                        labels = mapOf(
                            "per_person" to stringApp(R.string.pkg_price_per_person),
                            "group" to stringApp(R.string.pkg_price_group),
                        ),
                        onToggle = { priceType = it },
                    )
                    OutlinedTextField(
                        value = priceUsd,
                        onValueChange = { priceUsd = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.label_price_usd_required)) },
                        placeholder = { Text(stringApp(R.string.hint_tour_price)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = priceLabel,
                        onValueChange = { priceLabel = it },
                        label = { Text(stringApp(R.string.label_price_note)) },
                        placeholder = { Text(stringApp(R.string.hint_price_note)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                1 -> {
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_transport),
                        options = TRANSPORT_OPTIONS,
                        selected = transport,
                        labels = optionLabels(TRANSPORT_OPTIONS),
                        onToggle = { key ->
                            transport = if (key in transport) transport - key else transport + key
                        },
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_accommodation),
                        options = ACCOMMODATION_TYPES,
                        selected = setOf(accommodationType),
                        singleSelect = true,
                        labels = optionLabels(ACCOMMODATION_TYPES),
                        onToggle = { accommodationType = it },
                    )
                    if (accommodationType == "Hotel" || accommodationType == "Resort") {
                        ChipToggleRow(
                            label = stringApp(R.string.pkg_stars),
                            options = STAR_OPTIONS,
                            selected = setOf(accommodationStars),
                            singleSelect = true,
                            onToggle = { accommodationStars = it },
                        )
                    }
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_occupancy),
                        options = OCCUPANCY_OPTIONS,
                        selected = setOf(occupancy),
                        singleSelect = true,
                        labels = OCCUPANCY_OPTIONS.associateWith {
                            stringApp(R.string.pkg_occupancy_per_room, it)
                        },
                        onToggle = { occupancy = it },
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_meals),
                        options = MEAL_OPTIONS,
                        selected = meals,
                        labels = optionLabels(MEAL_OPTIONS),
                        onToggle = { key ->
                            meals = if (key in meals) meals - key else meals + key
                        },
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_activities),
                        options = ACTIVITY_OPTIONS,
                        selected = activities,
                        labels = optionLabels(ACTIVITY_OPTIONS),
                        onToggle = { key ->
                            activities = if (key in activities) activities - key else activities + key
                        },
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_insurance),
                        options = listOf("yes", "no"),
                        selected = setOf(if (insurance) "yes" else "no"),
                        singleSelect = true,
                        labels = mapOf(
                            "yes" to stringApp(R.string.pkg_yes),
                            "no" to stringApp(R.string.pkg_no),
                        ),
                        onToggle = { insurance = it == "yes" },
                    )
                    AddCustomRow(
                        value = customInclusion,
                        onValueChange = { customInclusion = it },
                        onAdd = {
                            val v = customInclusion.trim()
                            if (v.isNotEmpty()) {
                                customInclusions = customInclusions + v
                                customInclusion = ""
                            }
                        },
                        label = stringApp(R.string.pkg_add_inclusion),
                    )
                    if (customInclusions.isNotEmpty()) {
                        Text(customInclusions.joinToString(" · "), style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(stringApp(R.string.pkg_exclusions), fontWeight = FontWeight.SemiBold)
                    ChipToggleRow(
                        label = null,
                        options = DEFAULT_EXCLUSIONS,
                        selected = exclusions,
                        labels = optionLabels(DEFAULT_EXCLUSIONS),
                        onToggle = { key ->
                            exclusions = if (key in exclusions) exclusions - key else exclusions + key
                        },
                    )
                    AddCustomRow(
                        value = customExclusion,
                        onValueChange = { customExclusion = it },
                        onAdd = {
                            val v = customExclusion.trim()
                            if (v.isNotEmpty()) {
                                exclusions = exclusions + v
                                customExclusion = ""
                            }
                        },
                        label = stringApp(R.string.pkg_add_exclusion),
                    )
                }

                2 -> {
                    itinerary.forEachIndexed { dayIndex, day ->
                        Text(
                            stringApp(R.string.pkg_day_n, dayIndex + 1),
                            fontWeight = FontWeight.SemiBold,
                        )
                        OutlinedTextField(
                            value = day.title,
                            onValueChange = { value ->
                                itinerary = itinerary.toMutableList().also {
                                    it[dayIndex] = day.copy(title = value)
                                }
                            },
                            label = { Text(stringApp(R.string.pkg_day_title)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        day.items.forEachIndexed { itemIndex, item ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = item.time,
                                    onValueChange = { value ->
                                        itinerary = itinerary.toMutableList().also { list ->
                                            val items = day.items.toMutableList()
                                            items[itemIndex] = item.copy(time = value.take(20))
                                            list[dayIndex] = day.copy(items = items)
                                        }
                                    },
                                    label = { Text(stringApp(R.string.pkg_time)) },
                                    modifier = Modifier.weight(0.35f),
                                    singleLine = true,
                                    placeholder = { Text("06:00") },
                                )
                                OutlinedTextField(
                                    value = item.activity,
                                    onValueChange = { value ->
                                        itinerary = itinerary.toMutableList().also { list ->
                                            val items = day.items.toMutableList()
                                            items[itemIndex] = item.copy(activity = value)
                                            list[dayIndex] = day.copy(items = items)
                                        }
                                    },
                                    label = { Text(stringApp(R.string.pkg_activity)) },
                                    modifier = Modifier.weight(0.65f),
                                    singleLine = true,
                                )
                            }
                            OutlinedTextField(
                                value = item.location,
                                onValueChange = { value ->
                                    itinerary = itinerary.toMutableList().also { list ->
                                        val items = day.items.toMutableList()
                                        items[itemIndex] = item.copy(location = value)
                                        list[dayIndex] = day.copy(items = items)
                                    }
                                },
                                label = { Text(stringApp(R.string.pkg_item_location)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        }
                        TextButton(
                            onClick = {
                                itinerary = itinerary.toMutableList().also { list ->
                                    list[dayIndex] = day.copy(
                                        items = day.items + TourItineraryItem(),
                                    )
                                }
                            },
                        ) {
                            Text(stringApp(R.string.pkg_add_activity))
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            if (itinerary.size < 21) {
                                itinerary = itinerary + TourItineraryDay(
                                    day = itinerary.size + 1,
                                    title = "Day ${itinerary.size + 1}",
                                    items = listOf(TourItineraryItem()),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringApp(R.string.pkg_add_day))
                    }
                }

                else -> {
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_deposit),
                        options = DEPOSIT_OPTIONS,
                        selected = setOf(depositPercent),
                        singleSelect = true,
                        labels = DEPOSIT_OPTIONS.associateWith { "$it%" },
                        onToggle = { depositPercent = it },
                    )
                    Text(stringApp(R.string.pkg_cancel_policy), fontWeight = FontWeight.SemiBold)
                    Text(
                        stringApp(R.string.pkg_cancel_policy_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ChipToggleRow(
                        label = stringApp(R.string.pkg_bring),
                        options = BRING_OPTIONS,
                        selected = whatToBring,
                        labels = optionLabels(BRING_OPTIONS),
                        onToggle = { key ->
                            whatToBring = if (key in whatToBring) whatToBring - key else whatToBring + key
                        },
                    )
                    AddCustomRow(
                        value = customBring,
                        onValueChange = { customBring = it },
                        onAdd = {
                            val v = customBring.trim()
                            if (v.isNotEmpty()) {
                                whatToBring = whatToBring + v
                                customBring = ""
                            }
                        },
                        label = stringApp(R.string.pkg_add_bring),
                    )
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
                        placeholder = { Text("@username or t.me/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = messenger,
                        onValueChange = { messenger = it.take(120) },
                        label = { Text(stringApp(R.string.pkg_contact_messenger)) },
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
                modifier = Modifier.fillMaxWidth(),
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
                            val pkg = buildPackage()
                            val usd = priceUsd.toDoubleOrNull() ?: return@Button
                            onSubmit(
                                TourPackageFormResult(
                                    title = title.trim(),
                                    description = description.trim(),
                                    location = location.trim(),
                                    priceUsd = usd,
                                    priceLabel = priceLabel.trim().ifBlank {
                                        val unit = if (priceType == "group") "/ group" else "/ person"
                                        "$${usd.toInt()} $unit"
                                    },
                                    duration = pkg.durationLabel().ifBlank { "1 day" },
                                    category = tourType,
                                    packageDetails = pkg,
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
                            isEditing -> stringApp(R.string.business_save_changes)
                            else -> stringApp(R.string.business_publish)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipToggleRow(
    label: String?,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    singleSelect: Boolean = false,
    labels: Map<String, String> = emptyMap(),
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!label.isNullOrBlank()) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option in selected,
                    onClick = {
                        if (singleSelect) onToggle(option) else onToggle(option)
                    },
                    label = { Text(labels[option] ?: option) },
                )
            }
        }
    }
}

@Composable
private fun AddCustomRow(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        TextButton(onClick = onAdd, modifier = Modifier.padding(top = 8.dp)) {
            Text(stringApp(R.string.pkg_add))
        }
    }
}
