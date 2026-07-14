package com.example.wanderlust

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.wanderlust.data.model.BillingPlan
import com.example.wanderlust.data.model.BusinessProfile
import com.example.wanderlust.data.model.BusinessTourRequest
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.repository.BusinessRepository
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.SettingsSectionTitle
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.launch

private data class CityOption(val name: String, val lat: Double, val lng: Double)

private val CAMBODIA_CITIES = listOf(
    CityOption("Phnom Penh", 11.5564, 104.9282),
    CityOption("Siem Reap", 13.3633, 103.8564),
    CityOption("Sihanoukville", 10.6253, 103.5234),
    CityOption("Battambang", 13.0957, 103.2022),
    CityOption("Kampot", 10.6104, 104.1810),
    CityOption("Kep", 10.4826, 104.3167),
    CityOption("Koh Kong", 11.6153, 102.9836),
    CityOption("Kratie", 12.4882, 106.0188),
    CityOption("Mondulkiri", 12.4550, 107.1880),
)

private val TOUR_CATEGORIES = listOf(
    "Temple", "Beach", "Nature", "Food", "Culture", "Adventure", "City", "Mountain",
)

private val TOUR_DURATIONS = listOf("Half day", "1 day", "2 days", "3 days", "Custom")

private val VEHICLE_TYPES = listOf(
    "SUV", "Sedan", "Van", "Minibus", "Pickup", "Tuk-tuk", "Motorbike", "Bus",
)

private val SEAT_OPTIONS = listOf("2", "4", "5", "7", "9", "12", "15")
private val TRANSMISSIONS = listOf("Automatic", "Manual")
private val FUEL_TYPES = listOf("Petrol", "Diesel", "Hybrid", "Electric")
private val RATE_UNITS = listOf("day", "hour", "trip")

@Composable
fun BusinessStudioScreen(
    onBack: () -> Unit,
    onNeedSubscribe: () -> Unit = {},
) {
    val repo = remember { BusinessRepository() }
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<BusinessProfile?>(null) }
    var myTours by remember { mutableStateOf<List<Tour>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPostForm by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelMessage by remember { mutableStateOf<String?>(null) }
    var canceling by remember { mutableStateOf(false) }
    var editingTour by remember { mutableStateOf<Tour?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }
    var savingEdit by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Temple") }
    var location by remember { mutableStateOf("Phnom Penh") }
    var priceLabel by remember { mutableStateOf("") }
    var priceUsd by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("1 day") }
    var vehicleType by remember { mutableStateOf("SUV") }
    var seats by remember { mutableStateOf("7") }
    var transmission by remember { mutableStateOf("Automatic") }
    var fuelType by remember { mutableStateOf("Petrol") }
    var rateUnit by remember { mutableStateOf("day") }
    var serviceArea by remember { mutableStateOf("Phnom Penh") }
    var posting by remember { mutableStateOf(false) }

    val isTransport = profile?.businessSubtype.equals("TRANSPORT", ignoreCase = true)

    fun cityCoords(city: String): Pair<Double, Double> {
        val match = CAMBODIA_CITIES.firstOrNull { it.name.equals(city, ignoreCase = true) }
        return (match?.lat ?: 11.5564) to (match?.lng ?: 104.9282)
    }

    fun resetForm() {
        title = ""
        description = ""
        category = "Temple"
        location = "Phnom Penh"
        priceLabel = ""
        priceUsd = ""
        duration = "1 day"
        vehicleType = "SUV"
        seats = "7"
        transmission = "Automatic"
        fuelType = "Petrol"
        rateUnit = "day"
        serviceArea = "Phnom Penh"
    }

    fun formReady(): Boolean {
        return Validation.validateTourPost(
            title = title,
            description = description,
            priceUsd = priceUsd,
            isTransport = isTransport,
            locationOrArea = if (isTransport) serviceArea else location,
            seats = seats,
        ) == null && (!isTransport || vehicleType.isNotBlank()) &&
            (!isTransport || transmission.isNotBlank()) &&
            (!isTransport || fuelType.isNotBlank()) &&
            (isTransport || category.isNotBlank()) &&
            (isTransport || duration.isNotBlank())
    }

    fun formError(): String? = Validation.validateTourPost(
        title = title,
        description = description,
        priceUsd = priceUsd,
        isTransport = isTransport,
        locationOrArea = if (isTransport) serviceArea else location,
        seats = seats,
    )

    fun reload() {
        scope.launch {
            loading = true
            error = null
            val me = repo.getBusinessProfile()
            val tours = repo.getMyTours()
            me.onSuccess { profile = it }.onFailure { error = it.message }
            tours.onSuccess { myTours = it }
            loading = false
        }
    }

    LaunchedEffect(Unit) { reload() }

    StickyScrollScreen(
        title = stringLocalized(R.string.business_studio_title, R.string.business_studio_title_kh),
        onBack = onBack,
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }
            val sub = profile?.subscription
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        profile?.companyName?.ifBlank { profile?.name }.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        if (isTransport) {
                            stringLocalized(
                                R.string.register_biz_transport,
                                R.string.register_biz_transport_kh,
                            )
                        } else {
                            stringLocalized(R.string.register_biz_tours, R.string.register_biz_tours_kh)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when {
                            sub?.canPost == true && sub.cancelAtPeriodEnd ->
                                stringApp(
                                    R.string.business_sub_canceled_until,
                                    sub.expiresAt.orEmpty().take(10),
                                )
                            sub?.canPost == true ->
                                stringApp(
                                    R.string.business_sub_active_until,
                                    sub.expiresAt.orEmpty().take(10),
                                )
                            else ->
                                stringLocalized(R.string.business_sub_needed, R.string.business_sub_needed_kh)
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(10.dp))
                    if (sub?.canPost != true) {
                        Button(onClick = onNeedSubscribe, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringLocalized(
                                    R.string.business_subscribe_cta,
                                    R.string.business_subscribe_cta_kh,
                                ),
                            )
                        }
                    } else {
                        Button(
                            onClick = { showPostForm = !showPostForm },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                when {
                                    showPostForm ->
                                        stringLocalized(
                                            R.string.business_cancel_post,
                                            R.string.business_cancel_post_kh,
                                        )
                                    isTransport ->
                                        stringLocalized(
                                            R.string.business_post_vehicle,
                                            R.string.business_post_vehicle_kh,
                                        )
                                    else ->
                                        stringLocalized(
                                            R.string.business_post_tour,
                                            R.string.business_post_tour_kh,
                                        )
                                },
                            )
                        }
                    }
                }
            }


            val expiryLabel = sub?.expiresAt.orEmpty().take(10)
            if (sub != null && sub.status != "none") {
                SettingsSectionTitle(stringApp(R.string.business_manage_sub))
                StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val planLabel = if (AppLocale.isKhmer) {
                            sub.planNameKh ?: sub.planName
                        } else {
                            sub.planName ?: sub.planId
                        }
                        Text(
                            planLabel.orEmpty(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            stringApp(R.string.business_benefits_title),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        val benefits = if (AppLocale.isKhmer && sub.benefitsKh.isNotEmpty()) {
                            sub.benefitsKh
                        } else {
                            sub.benefits
                        }
                        benefits.forEach { benefit ->
                            Text("• $benefit", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            stringApp(R.string.business_no_refund_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        cancelMessage?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        OutlinedButton(
                            onClick = onNeedSubscribe,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringApp(R.string.business_renew_plan))
                        }
                        if (sub.canPost && !sub.cancelAtPeriodEnd) {
                            OutlinedButton(
                                enabled = !canceling,
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    if (canceling) "…"
                                    else stringApp(R.string.business_cancel_subscription),
                                )
                            }
                        }
                    }
                }
            }

            if (showCancelDialog) {
                val cancelSuccessFallback = stringApp(R.string.business_cancel_success, expiryLabel)
                AlertDialog(
                    onDismissRequest = { if (!canceling) showCancelDialog = false },
                    title = { Text(stringApp(R.string.business_cancel_confirm_title)) },
                    text = {
                        Text(stringApp(R.string.business_cancel_confirm_body, expiryLabel))
                    },
                    confirmButton = {
                        TextButton(
                            enabled = !canceling,
                            onClick = {
                                scope.launch {
                                    canceling = true
                                    repo.cancelSubscription()
                                        .onSuccess { result ->
                                            cancelMessage = when {
                                                AppLocale.isKhmer && !result.messageKh.isNullOrBlank() ->
                                                    result.messageKh
                                                !AppLocale.isKhmer && !result.message.isNullOrBlank() ->
                                                    result.message
                                                AppLocale.isKhmer && !result.message.isNullOrBlank() &&
                                                    result.messageKh.isNullOrBlank() -> result.message
                                                else -> cancelSuccessFallback
                                            }
                                            showCancelDialog = false
                                            reload()
                                        }
                                        .onFailure { error = it.message }
                                    canceling = false
                                }
                            },
                        ) {
                            Text(stringApp(R.string.business_cancel_confirm_yes))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !canceling,
                            onClick = { showCancelDialog = false },
                        ) {
                            Text(stringApp(R.string.btn_keep_subscription))
                        }
                    },
                )
            }

            editingTour?.let { tour ->
                AlertDialog(
                    onDismissRequest = { if (!savingEdit) editingTour = null },
                    title = { Text(stringApp(R.string.business_edit_listing)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editTitle,
                                onValueChange = { editTitle = it },
                                label = { Text(stringApp(R.string.label_title)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = editDescription,
                                onValueChange = { editDescription = it },
                                label = { Text(stringApp(R.string.label_description)) },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = editLocation,
                                onValueChange = { editLocation = it },
                                label = { Text(stringApp(R.string.label_location)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = editPrice,
                                onValueChange = { editPrice = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text(stringApp(R.string.label_price_usd)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            enabled = !savingEdit &&
                                Validation.requireTourTitle(editTitle) == null &&
                                Validation.requireTourDescription(editDescription) == null &&
                                (editPrice.isBlank() || Validation.requirePriceUsd(editPrice) == null),
                            onClick = {
                                val err = Validation.requireTourTitle(editTitle)
                                    ?: Validation.requireTourDescription(editDescription)
                                    ?: editPrice.takeIf { it.isNotBlank() }?.let { Validation.requirePriceUsd(it) }
                                if (err != null) {
                                    error = err
                                    return@TextButton
                                }
                                scope.launch {
                                    savingEdit = true
                                    repo.updateTour(
                                        tour.id,
                                        BusinessTourRequest(
                                            title = editTitle.trim(),
                                            description = editDescription.trim(),
                                            location = editLocation.trim(),
                                            priceUsd = editPrice.toDoubleOrNull(),
                                            category = tour.category,
                                            listingType = tour.listingType,
                                            vehicleType = tour.vehicleType,
                                            seats = tour.seats,
                                            transmission = tour.transmission,
                                            fuelType = tour.fuelType,
                                            rateUnit = tour.rateUnit,
                                            serviceArea = tour.serviceArea,
                                            duration = tour.duration,
                                            priceLabel = tour.priceLabel,
                                            status = tour.status,
                                        ),
                                    ).onSuccess {
                                        editingTour = null
                                        reload()
                                    }.onFailure { error = it.message }
                                    savingEdit = false
                                }
                            },
                        ) {
                            Text(stringApp(R.string.business_save_changes))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            enabled = !savingEdit,
                            onClick = { editingTour = null },
                        ) {
                            Text(stringApp(R.string.business_cancel_post))
                        }
                    },
                )
            }

            if (showPostForm && sub?.canPost == true) {
                SettingsSectionTitle(
                    if (isTransport) {
                        stringLocalized(R.string.business_new_vehicle, R.string.business_new_vehicle_kh)
                    } else {
                        stringLocalized(R.string.business_new_tour, R.string.business_new_tour_kh)
                    },
                )
                Text(
                    stringApp(
                        if (isTransport) R.string.business_form_hint_vehicle
                        else R.string.business_form_hint_tour,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            stringApp(
                                if (isTransport) R.string.label_vehicle_title
                                else R.string.label_tour_title,
                            ),
                        )
                    },
                    placeholder = {
                        Text(
                            stringApp(
                                if (isTransport) R.string.hint_vehicle_title
                                else R.string.hint_tour_title,
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            stringLocalized(
                                R.string.label_description,
                                R.string.label_description_kh,
                            ),
                        )
                    },
                    placeholder = {
                        Text(
                            stringApp(
                                if (isTransport) R.string.hint_vehicle_description
                                else R.string.hint_tour_description,
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                )
                Spacer(Modifier.height(12.dp))

                if (isTransport) {
                    OptionChipRow(
                        label = stringApp(R.string.label_vehicle_type),
                        options = VEHICLE_TYPES,
                        selected = vehicleType,
                        onSelect = { vehicleType = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringApp(R.string.label_seats),
                        options = SEAT_OPTIONS,
                        selected = seats,
                        onSelect = { seats = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringLocalized(R.string.label_transmission, R.string.label_transmission_kh),
                        options = TRANSMISSIONS,
                        selected = transmission,
                        onSelect = { transmission = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringLocalized(R.string.label_fuel, R.string.label_fuel_kh),
                        options = FUEL_TYPES,
                        selected = fuelType,
                        onSelect = { fuelType = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringApp(R.string.label_rate_unit),
                        options = RATE_UNITS,
                        selected = rateUnit,
                        onSelect = { rateUnit = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceUsd,
                        onValueChange = { priceUsd = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.label_price_usd_required)) },
                        placeholder = { Text(stringApp(R.string.hint_vehicle_price)) },
                        supportingText = {
                            Text("USD / $rateUnit")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringApp(R.string.label_service_area),
                        options = CAMBODIA_CITIES.map { it.name },
                        selected = serviceArea,
                        onSelect = { serviceArea = it },
                    )
                } else {
                    OptionChipRow(
                        label = stringApp(R.string.label_pick_category),
                        options = TOUR_CATEGORIES,
                        selected = category,
                        onSelect = { category = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringApp(R.string.label_pick_city),
                        options = CAMBODIA_CITIES.map { it.name },
                        selected = location,
                        onSelect = { location = it },
                    )
                    Spacer(Modifier.height(8.dp))
                    OptionChipRow(
                        label = stringApp(R.string.label_pick_duration),
                        options = TOUR_DURATIONS,
                        selected = if (TOUR_DURATIONS.any { it.equals(duration, true) && it != "Custom" }) {
                            duration
                        } else {
                            "Custom"
                        },
                        onSelect = { picked ->
                            duration = if (picked == "Custom") "" else picked
                        },
                    )
                    val isCustomDuration =
                        TOUR_DURATIONS.none { it.equals(duration, true) && it != "Custom" }
                    if (isCustomDuration) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text(stringApp(R.string.label_pick_duration)) },
                            placeholder = { Text("e.g. 5 hours · 4 nights") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceUsd,
                        onValueChange = { priceUsd = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringApp(R.string.label_price_usd_required)) },
                        placeholder = { Text(stringApp(R.string.hint_tour_price)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceLabel,
                        onValueChange = { priceLabel = it },
                        label = { Text(stringApp(R.string.label_price_note)) },
                        placeholder = { Text(stringApp(R.string.hint_price_note)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    enabled = !posting && formReady(),
                    onClick = {
                        scope.launch {
                            posting = true
                            error = null
                            val usd = priceUsd.toDoubleOrNull()
                            if (usd == null || !formReady()) {
                                error = null
                                posting = false
                                return@launch
                            }
                            val city = if (isTransport) serviceArea else location
                            val (lat, lng) = cityCoords(city)
                            val request = if (isTransport) {
                                BusinessTourRequest(
                                    title = title.trim(),
                                    description = description.trim(),
                                    category = vehicleType.trim(),
                                    location = serviceArea.trim(),
                                    priceUsd = usd,
                                    listingType = "VEHICLE",
                                    vehicleType = vehicleType.trim(),
                                    seats = seats.toIntOrNull(),
                                    transmission = transmission.trim(),
                                    fuelType = fuelType.trim(),
                                    rateUnit = rateUnit.trim().ifBlank { "day" },
                                    serviceArea = serviceArea.trim(),
                                    latitude = lat,
                                    longitude = lng,
                                )
                            } else {
                                BusinessTourRequest(
                                    title = title.trim(),
                                    description = description.trim(),
                                    category = category.trim().ifBlank { "Tour" },
                                    location = location.trim(),
                                    priceLabel = priceLabel.trim(),
                                    priceUsd = usd,
                                    duration = duration.trim().ifBlank { "1 day" },
                                    listingType = "TOUR",
                                    latitude = lat,
                                    longitude = lng,
                                )
                            }
                            repo.createTour(request)
                                .onSuccess {
                                    resetForm()
                                    showPostForm = false
                                    reload()
                                }
                                .onFailure { error = it.message }
                            posting = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        when {
                            posting -> "…"
                            isTransport ->
                                stringLocalized(
                                    R.string.business_publish_vehicle,
                                    R.string.business_publish_vehicle_kh,
                                )
                            else ->
                                stringLocalized(R.string.business_publish, R.string.business_publish_kh)
                        },
                    )
                }
                if (!formReady()) {
                    Text(
                        formError() ?: stringApp(R.string.business_form_incomplete),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            SettingsSectionTitle(
                stringApp(R.string.business_manage_posts) + " (${myTours.size})",
            )
            if (myTours.isEmpty()) {
                Text(
                    stringApp(R.string.business_no_listings),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                myTours.forEach { tour ->
                    val published = tour.status.equals("published", ignoreCase = true)
                    StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(tour.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                listOfNotNull(
                                    if (published) stringApp(R.string.business_post_published)
                                    else stringApp(R.string.business_post_hidden),
                                    tour.listingType.takeIf { it.isNotBlank() },
                                    tour.vehicleType.takeIf { it.isNotBlank() },
                                    tour.category.takeIf { it.isNotBlank() && tour.listingType != "VEHICLE" },
                                    tour.location.takeIf { it.isNotBlank() },
                                    tour.duration.takeIf { it.isNotBlank() },
                                    tour.priceLabel.takeIf { it.isNotBlank() }
                                        ?: tour.priceUsd?.let { "$$it" },
                                    "★ ${tour.rating} (${tour.ratingCount})",
                                ).joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editTitle = tour.title
                                        editDescription = tour.description
                                        editLocation = tour.location
                                        editPrice = tour.priceUsd?.toString().orEmpty()
                                        editingTour = tour
                                    },
                                ) {
                                    Text(stringApp(R.string.business_post_edit))
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val nextStatus = if (published) "draft" else "published"
                                            repo.updateTour(
                                                tour.id,
                                                BusinessTourRequest(
                                                    title = tour.title,
                                                    description = tour.description,
                                                    category = tour.category,
                                                    location = tour.location,
                                                    priceLabel = tour.priceLabel,
                                                    priceUsd = tour.priceUsd,
                                                    duration = tour.duration,
                                                    listingType = tour.listingType,
                                                    vehicleType = tour.vehicleType,
                                                    seats = tour.seats,
                                                    transmission = tour.transmission,
                                                    fuelType = tour.fuelType,
                                                    rateUnit = tour.rateUnit,
                                                    serviceArea = tour.serviceArea,
                                                    status = nextStatus,
                                                ),
                                            ).onSuccess { reload() }
                                                .onFailure { error = it.message }
                                        }
                                    },
                                ) {
                                    Text(
                                        stringApp(
                                            if (published) R.string.business_post_hide
                                            else R.string.business_post_publish,
                                        ),
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            repo.deleteTour(tour.id)
                                            reload()
                                        }
                                    },
                                ) {
                                    Text(stringLocalized(R.string.btn_delete, R.string.btn_delete_kh))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionChipRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected.equals(option, ignoreCase = true),
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                )
            }
        }
    }
}

@Composable
fun BusinessSubscribeScreen(
    onBack: () -> Unit,
    onPaid: () -> Unit,
) {
    val repo = remember { BusinessRepository() }
    val scope = rememberCoroutineScope()
    var plans by remember { mutableStateOf<List<BillingPlan>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var payingId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repo.getPlans()
            .onSuccess {
                plans = it
                loading = false
            }
            .onFailure {
                error = it.message
                loading = false
            }
    }

    StickyScrollScreen(
        title = stringLocalized(R.string.business_plans_title, R.string.business_plans_title_kh),
        onBack = onBack,
    ) {
        Text(
            stringLocalized(R.string.business_sandbox_hint, R.string.business_sandbox_hint_kh),
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
        plans.forEach { plan ->
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (AppLocale.isKhmer && plan.nameKh.isNotBlank()) plan.nameKh else plan.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("\$${plan.priceUsd.toInt()} USD", fontWeight = FontWeight.SemiBold)
                    Text(
                        if (AppLocale.isKhmer && plan.descriptionKh.isNotBlank()) {
                            plan.descriptionKh
                        } else {
                            plan.description
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringApp(R.string.business_benefits_title),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    val planBenefits = if (AppLocale.isKhmer && plan.benefitsKh.isNotEmpty()) {
                        plan.benefitsKh
                    } else {
                        plan.benefits
                    }
                    planBenefits.forEach { benefit ->
                        Text("• $benefit", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        stringApp(R.string.business_no_refund_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        enabled = payingId == null,
                        onClick = {
                            scope.launch {
                                payingId = plan.id
                                error = null
                                repo.sandboxPay(plan.id)
                                    .onSuccess {
                                        message = it.message
                                        payingId = null
                                        onPaid()
                                    }
                                    .onFailure {
                                        error = it.message
                                        payingId = null
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (payingId == plan.id) {
                                "…"
                            } else {
                                stringLocalized(
                                    R.string.business_pay_sandbox,
                                    R.string.business_pay_sandbox_kh,
                                )
                            },
                        )
                    }
                }
            }
        }
        Text(
            stringLocalized(R.string.business_bakong_soon, R.string.business_bakong_soon_kh),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
