package com.example.wanderlust.ui.screens.business

import com.example.wanderlust.ui.screens.business.forms.RentalPostForm
import com.example.wanderlust.ui.screens.business.forms.TourPackageFormResult
import com.example.wanderlust.ui.screens.business.forms.TourPackagePostForm
import com.example.wanderlust.ui.screens.business.forms.TripFormResult
import com.example.wanderlust.ui.screens.business.forms.TripPostForm
import com.example.wanderlust.ui.screens.business.forms.RentalFormResult

import com.example.wanderlust.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.wanderlust.data.model.BakongCreatePaymentResponse
import com.example.wanderlust.data.model.BankDeepLink
import com.example.wanderlust.data.model.BillingPlan
import com.example.wanderlust.data.model.BusinessProfile
import com.example.wanderlust.data.model.BusinessProfileUpdateRequest
import com.example.wanderlust.data.model.BusinessTourRequest
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.model.routeLabel
import androidx.compose.runtime.key
import com.example.wanderlust.data.repository.BusinessRepository
import com.example.wanderlust.locale.AppLocale
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.KhqrPaymentCard
import com.example.wanderlust.ui.components.SettingsSectionTitle
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.util.KhqrBitmap
import com.example.wanderlust.util.Validation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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
    /** TRANSPORT only: "trip" (fixed-route bus) or "rental" (car charter). */
    var transportPostKind by remember { mutableStateOf("trip") }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelMessage by remember { mutableStateOf<String?>(null) }
    var canceling by remember { mutableStateOf(false) }
    var editingTour by remember { mutableStateOf<Tour?>(null) }
    var deletingTour by remember { mutableStateOf<Tour?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }
    var savingEdit by remember { mutableStateOf(false) }
    var companyNameDraft by remember { mutableStateOf("") }
    var savingCompany by remember { mutableStateOf(false) }
    var companySaved by remember { mutableStateOf(false) }

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
    LaunchedEffect(profile?.companyName) {
        if (editingTour == null) {
            companyNameDraft = profile?.companyName.orEmpty()
        }
    }

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
                                        stringApp(R.string.business_post_trip)
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

            SettingsSectionTitle(stringApp(R.string.business_company_settings))
            StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = companyNameDraft,
                        onValueChange = { companyNameDraft = it.take(150) },
                        label = { Text(stringApp(R.string.business_company_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (companySaved) {
                        Text(
                            stringApp(R.string.business_company_saved),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Button(
                        enabled = !savingCompany && Validation.requireCompany(companyNameDraft) == null,
                        onClick = {
                            val err = Validation.requireCompany(companyNameDraft)
                            if (err != null) {
                                error = err
                                return@Button
                            }
                            scope.launch {
                                savingCompany = true
                                companySaved = false
                                error = null
                                repo.updateBusinessProfile(
                                    BusinessProfileUpdateRequest(companyName = companyNameDraft.trim()),
                                ).onSuccess {
                                    profile = it
                                    companySaved = true
                                }.onFailure { error = it.message }
                                savingCompany = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (savingCompany) "…" else stringApp(R.string.business_save_changes),
                        )
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
                SettingsSectionTitle(stringApp(R.string.business_edit_listing))
                key(tour.id) {
                    when (tour.listingType.uppercase()) {
                        "TRIP" -> {
                            TripPostForm(
                                posting = savingEdit,
                                isEditing = true,
                                initialPriceUsd = tour.priceUsd,
                                initialTrip = tour.tripDetails,
                                onCancel = { editingTour = null },
                                onSubmit = { result ->
                                    scope.launch {
                                        savingEdit = true
                                        error = null
                                        val trip = result.tripDetails
                                        val (lat, lng) = cityCoords(trip.departureCity)
                                        repo.updateTour(
                                            tour.id,
                                            BusinessTourRequest(
                                                title = trip.routeLabel().ifBlank { tour.title },
                                                description = "${trip.vehicleType} · ${trip.totalSeats} seats",
                                                category = "Bus Trip",
                                                location = trip.routeLabel(),
                                                priceLabel = "$${result.priceUsd.toInt()} / seat",
                                                priceUsd = result.priceUsd,
                                                duration = "${trip.departureTime}${if (trip.arrivalTime.isNotBlank()) " → ${trip.arrivalTime}" else ""}",
                                                listingType = "TRIP",
                                                vehicleType = trip.vehicleType,
                                                seats = trip.totalSeats,
                                                rateUnit = "seat",
                                                serviceArea = trip.departureCity,
                                                latitude = lat,
                                                longitude = lng,
                                                status = if (trip.active) "published" else "draft",
                                                imageUrl = trip.imageUrls.firstOrNull().orEmpty(),
                                                tripDetails = trip,
                                            ),
                                        ).onSuccess {
                                            editingTour = null
                                            reload()
                                        }.onFailure { error = it.message }
                                        savingEdit = false
                                    }
                                },
                            )
                        }
                        "RENTAL" -> {
                            RentalPostForm(
                                posting = savingEdit,
                                isEditing = true,
                                initialServiceArea = tour.serviceArea.ifBlank { tour.location },
                                initialDetails = tour.rentalDetails,
                                onCancel = { editingTour = null },
                                onSubmit = { result ->
                                    scope.launch {
                                        savingEdit = true
                                        error = null
                                        val r = result.rentalDetails
                                        val (lat, lng) = cityCoords(result.serviceArea)
                                        val modes = listOfNotNull(
                                            if (r.withDriver) "With driver" else null,
                                            if (r.selfDrive) "Self-drive" else null,
                                        ).joinToString(" · ")
                                        repo.updateTour(
                                            tour.id,
                                            BusinessTourRequest(
                                                title = r.makeModel.ifBlank { tour.title },
                                                description = listOfNotNull(
                                                    r.condition.takeIf { it.isNotBlank() },
                                                    modes.takeIf { it.isNotBlank() },
                                                ).joinToString(" · ").ifBlank { tour.description },
                                                category = r.vehicleType.ifBlank { tour.category },
                                                location = result.serviceArea,
                                                priceLabel = "$${result.priceUsd.toInt()} / day",
                                                priceUsd = result.priceUsd,
                                                duration = "Per day",
                                                imageUrl = r.imageUrls.firstOrNull().orEmpty(),
                                                listingType = "RENTAL",
                                                vehicleType = r.vehicleType,
                                                seats = r.seats,
                                                rateUnit = "day",
                                                serviceArea = result.serviceArea,
                                                latitude = lat,
                                                longitude = lng,
                                                status = if (r.available) "published" else "draft",
                                                rentalDetails = r,
                                            ),
                                        ).onSuccess {
                                            editingTour = null
                                            reload()
                                        }.onFailure { error = it.message }
                                        savingEdit = false
                                    }
                                },
                            )
                        }
                        else -> {
                            TourPackagePostForm(
                                posting = savingEdit,
                                isEditing = true,
                                initialTitle = tour.title,
                                initialDescription = tour.description,
                                initialLocation = tour.location,
                                initialPriceUsd = tour.priceUsd,
                                initialPriceLabel = tour.priceLabel,
                                initialDetails = tour.packageDetails,
                                onCancel = { editingTour = null },
                                onSubmit = { result ->
                                    scope.launch {
                                        savingEdit = true
                                        error = null
                                        val (lat, lng) = cityCoords(result.location)
                                        repo.updateTour(
                                            tour.id,
                                            BusinessTourRequest(
                                                title = result.title,
                                                description = result.description,
                                                category = result.category,
                                                location = result.location,
                                                priceLabel = result.priceLabel,
                                                priceUsd = result.priceUsd,
                                                duration = result.duration,
                                                imageUrl = result.packageDetails.imageUrls.firstOrNull().orEmpty(),
                                                listingType = "TOUR",
                                                latitude = lat,
                                                longitude = lng,
                                                status = tour.status,
                                                packageDetails = result.packageDetails,
                                            ),
                                        ).onSuccess {
                                            editingTour = null
                                            reload()
                                        }.onFailure { error = it.message }
                                        savingEdit = false
                                    }
                                },
                            )
                        }
                    }
                }
            }

            deletingTour?.let { tour ->
                AlertDialog(
                    onDismissRequest = { deletingTour = null },
                    title = { Text(stringApp(R.string.business_delete_confirm_title)) },
                    text = { Text(stringApp(R.string.business_delete_confirm_body)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    error = null
                                    repo.deleteTour(tour.id)
                                        .onSuccess {
                                            deletingTour = null
                                            reload()
                                        }
                                        .onFailure { error = it.message }
                                }
                            },
                        ) {
                            Text(stringLocalized(R.string.btn_delete, R.string.btn_delete_kh))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deletingTour = null }) {
                            Text(stringApp(R.string.business_cancel_post))
                        }
                    },
                )
            }

            if (showPostForm && sub?.canPost == true && editingTour == null) {
                if (isTransport) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = transportPostKind == "trip",
                            onClick = { transportPostKind = "trip" },
                            label = { Text(stringApp(R.string.business_post_mode_trip)) },
                        )
                        FilterChip(
                            selected = transportPostKind == "rental",
                            onClick = { transportPostKind = "rental" },
                            label = { Text(stringApp(R.string.business_post_mode_rental)) },
                        )
                    }
                }
                SettingsSectionTitle(
                    when {
                        isTransport && transportPostKind == "rental" ->
                            stringApp(R.string.business_new_rental)
                        isTransport ->
                            stringApp(R.string.business_new_trip)
                        else ->
                            stringLocalized(R.string.business_new_tour, R.string.business_new_tour_kh)
                    },
                )
                Text(
                    stringApp(
                        when {
                            isTransport && transportPostKind == "rental" ->
                                R.string.business_form_hint_rental
                            isTransport ->
                                R.string.business_form_hint_trip
                            else ->
                                R.string.business_form_hint_tour
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                when {
                    isTransport && transportPostKind == "rental" -> {
                        RentalPostForm(
                            posting = posting,
                            onCancel = {
                                showPostForm = false
                                resetForm()
                            },
                            onSubmit = { result ->
                                scope.launch {
                                    posting = true
                                    error = null
                                    val r = result.rentalDetails
                                    val (lat, lng) = cityCoords(result.serviceArea)
                                    val modes = listOfNotNull(
                                        if (r.withDriver) "With driver" else null,
                                        if (r.selfDrive) "Self-drive" else null,
                                    ).joinToString(" · ")
                                    repo.createTour(
                                        BusinessTourRequest(
                                            title = r.makeModel,
                                            description = listOfNotNull(
                                                r.condition.takeIf { it.isNotBlank() },
                                                modes.takeIf { it.isNotBlank() },
                                                listOfNotNull(
                                                    r.year.takeIf { it.isNotBlank() },
                                                    r.color.takeIf { it.isNotBlank() },
                                                ).joinToString(" · ").takeIf { it.isNotBlank() },
                                            ).joinToString(" · "),
                                            category = r.vehicleType.ifBlank { "Car Rental" },
                                            location = result.serviceArea,
                                            priceLabel = "$${result.priceUsd.toInt()} / day",
                                            priceUsd = result.priceUsd,
                                            duration = "Per day",
                                            imageUrl = r.imageUrls.firstOrNull().orEmpty(),
                                            listingType = "RENTAL",
                                            vehicleType = r.vehicleType,
                                            seats = r.seats,
                                            rateUnit = "day",
                                            serviceArea = result.serviceArea,
                                            latitude = lat,
                                            longitude = lng,
                                            status = if (r.available) "published" else "draft",
                                            rentalDetails = r,
                                        ),
                                    ).onSuccess {
                                        resetForm()
                                        showPostForm = false
                                        reload()
                                    }.onFailure { error = it.message }
                                    posting = false
                                }
                            },
                        )
                    }
                    isTransport -> {
                        TripPostForm(
                            posting = posting,
                            onCancel = {
                                showPostForm = false
                                resetForm()
                            },
                            onSubmit = { result ->
                                scope.launch {
                                    posting = true
                                    error = null
                                    val trip = result.tripDetails
                                    val (lat, lng) = cityCoords(trip.departureCity)
                                    repo.createTour(
                                        BusinessTourRequest(
                                            title = trip.routeLabel(),
                                            description = "${trip.vehicleType} · ${trip.totalSeats} seats",
                                            category = "Bus Trip",
                                            location = trip.routeLabel(),
                                            priceLabel = "$${result.priceUsd.toInt()} / seat",
                                            priceUsd = result.priceUsd,
                                            duration = "${trip.departureTime}${if (trip.arrivalTime.isNotBlank()) " → ${trip.arrivalTime}" else ""}",
                                            listingType = "TRIP",
                                            vehicleType = trip.vehicleType,
                                            seats = trip.totalSeats,
                                            rateUnit = "seat",
                                            serviceArea = trip.departureCity,
                                            latitude = lat,
                                            longitude = lng,
                                            status = if (trip.active) "published" else "draft",
                                            imageUrl = trip.imageUrls.firstOrNull().orEmpty(),
                                            tripDetails = trip,
                                        ),
                                    ).onSuccess {
                                        resetForm()
                                        showPostForm = false
                                        reload()
                                    }.onFailure { error = it.message }
                                    posting = false
                                }
                            },
                        )
                    }
                    else -> {
                        TourPackagePostForm(
                            posting = posting,
                            onCancel = {
                                showPostForm = false
                                resetForm()
                            },
                            onSubmit = { result ->
                                scope.launch {
                                    posting = true
                                    error = null
                                    val (lat, lng) = cityCoords(result.location)
                                    repo.createTour(
                                        BusinessTourRequest(
                                            title = result.title,
                                            description = result.description,
                                            category = result.category,
                                            location = result.location,
                                            priceLabel = result.priceLabel,
                                            priceUsd = result.priceUsd,
                                            duration = result.duration,
                                            imageUrl = result.packageDetails.imageUrls.firstOrNull().orEmpty(),
                                            listingType = "TOUR",
                                            latitude = lat,
                                            longitude = lng,
                                            packageDetails = result.packageDetails,
                                        ),
                                    ).onSuccess {
                                        resetForm()
                                        showPostForm = false
                                        reload()
                                    }.onFailure { error = it.message }
                                    posting = false
                                }
                            },
                        )
                    }
                }
            }

            SettingsSectionTitle(
                stringApp(R.string.business_manage_posts) + " (${myTours.size})",
            )
            Text(
                stringApp(R.string.business_manage_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
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
                                        showPostForm = false
                                        editingTour = tour
                                    },
                                ) {
                                    Text(stringApp(R.string.business_post_edit))
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val nextStatus = if (published) "draft" else "published"
                                            if (nextStatus == "published" && sub?.canPost != true) {
                                                error = "Active subscription required to publish. Renew your plan."
                                                onNeedSubscribe()
                                                return@launch
                                            }
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
                                                    packageDetails = tour.packageDetails,
                                                    tripDetails = tour.tripDetails,
                                                    rentalDetails = tour.rentalDetails,
                                                ),
                                            ).onSuccess { reload() }
                                                .onFailure { err ->
                                                    error = err.message
                                                    if (err.message?.contains("subscription", ignoreCase = true) == true) {
                                                        onNeedSubscribe()
                                                    }
                                                }
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
                                    onClick = { deletingTour = tour },
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repo = remember { BusinessRepository() }
    val scope = rememberCoroutineScope()
    var plans by remember { mutableStateOf<List<BillingPlan>>(emptyList()) }
    var bakongEnabled by remember { mutableStateOf(false) }
    var bakongAutoVerify by remember { mutableStateOf(false) }
    var bakongMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var payingId by remember { mutableStateOf<String?>(null) }
    var payCurrency by remember { mutableStateOf("USD") }
    var usdToKhrRate by remember { mutableStateOf(4100.0) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var checkout by remember { mutableStateOf<BakongCreatePaymentResponse?>(null) }
    var confirming by remember { mutableStateOf(false) }
    var awaitingBankReturn by remember { mutableStateOf(false) }
    var paymentSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun finishSuccess(msg: String?) {
        paymentSuccess = true
        successMessage = msg
        checkout = null
        awaitingBankReturn = false
        confirming = false
        Toast.makeText(
            context,
            msg ?: context.getString(R.string.business_khqr_success_title),
            Toast.LENGTH_LONG,
        ).show()
    }

    fun confirmPayment(paymentId: String, fromAuto: Boolean = false) {
        if (confirming || paymentSuccess) return
        scope.launch {
            confirming = true
            error = null
            if (fromAuto) {
                message = null
            }
            repo.confirmBakongPayment(paymentId)
                .onSuccess { conf ->
                    confirming = false
                    awaitingBankReturn = false
                    val msg = if (AppLocale.isKhmer && !conf.messageKh.isNullOrBlank()) {
                        conf.messageKh
                    } else {
                        conf.message
                    }
                    val reallyPaid = conf.paid || conf.alreadyPaid ||
                        conf.payment?.status.equals("succeeded", ignoreCase = true) == true
                    when {
                        reallyPaid && !conf.pendingAdmin -> finishSuccess(msg)
                        conf.pendingAdmin -> message = msg
                        else -> {
                            // Not verified yet — do NOT grant subscription in UI
                            message = msg
                                ?: context.getString(R.string.business_khqr_not_paid_yet)
                        }
                    }
                }
                .onFailure {
                    confirming = false
                    if (!fromAuto) {
                        error = it.message
                    }
                }
        }
    }

    LaunchedEffect(Unit) {
        repo.getPlans()
            .onSuccess { res ->
                plans = res.plans
                usdToKhrRate = res.usdToKhrRate.takeIf { it > 0 } ?: 4100.0
                val bakong = res.paymentProviders?.bakong
                bakongEnabled = bakong?.enabled == true
                bakongAutoVerify = bakong?.autoVerify == true
                if (bakong?.usdToKhrRate != null && bakong.usdToKhrRate > 0) {
                    usdToKhrRate = bakong.usdToKhrRate
                }
                bakongMessage = if (AppLocale.isKhmer && !bakong?.messageKh.isNullOrBlank()) {
                    bakong?.messageKh
                } else {
                    bakong?.message
                }
                loading = false
            }
            .onFailure {
                error = it.message
                loading = false
            }
    }

    // When user returns from ABA / bank app → auto-confirm + success.
    DisposableEffect(lifecycleOwner, checkout?.payment?.id, awaitingBankReturn) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
            val id = checkout?.payment?.id
            if (awaitingBankReturn && !id.isNullOrBlank() && !confirming && !paymentSuccess) {
                confirmPayment(id, fromAuto = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // After success screen, leave subscribe flow.
    LaunchedEffect(paymentSuccess) {
        if (!paymentSuccess) return@LaunchedEffect
        delay(1600)
        onPaid()
    }

    val qrBitmap = remember(checkout?.khqr?.qr) {
        checkout?.khqr?.qr?.let { KhqrBitmap.encode(it) }
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

        if (paymentSuccess) {
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(
                    Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringLocalized(R.string.business_khqr_success_title, R.string.business_khqr_success_title_kh),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        successMessage
                            ?: stringLocalized(R.string.business_khqr_success_body, R.string.business_khqr_success_body_kh),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        checkout?.let { pay ->
            StitchGhostCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringLocalized(R.string.business_pay_khqr, R.string.business_pay_khqr_kh),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringLocalized(R.string.business_khqr_scan_hint, R.string.business_khqr_scan_hint_kh),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (bakongAutoVerify) {
                            stringLocalized(R.string.business_khqr_waiting_auto, R.string.business_khqr_waiting_auto_kh)
                        } else {
                            stringLocalized(R.string.business_khqr_waiting, R.string.business_khqr_waiting_kh)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(10.dp))
                    val amountLabel = when (pay.khqr?.currency?.uppercase()) {
                        "KHR" -> {
                            val khr = pay.khqr?.amountKhr?.takeIf { it > 0 }
                                ?: pay.khqr?.amount?.toInt()
                                ?: 0
                            "%,d ៛".format(khr)
                        }
                        else -> {
                            val amountText = String.format(
                                "%.2f",
                                pay.khqr?.amountUsd ?: pay.payment?.amountUsd ?: 0.0,
                            )
                            "$$amountText"
                        }
                    }
                    qrBitmap?.let { bmp ->
                        KhqrPaymentCard(
                            qrBitmap = bmp,
                            merchantName = pay.khqr?.merchantName?.ifBlank { "MAO SOKHUN" } ?: "MAO SOKHUN",
                            amountLabel = amountLabel,
                            billNumber = pay.payment?.billNumber,
                            currencyCode = pay.khqr?.currency ?: "USD",
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .padding(vertical = 4.dp),
                        )
                    }
                    if (confirming) {
                        Spacer(Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Text(
                            stringLocalized(R.string.business_khqr_checking, R.string.business_khqr_checking_kh),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    pay.deepLinks.forEach { link ->
                        val label = if (AppLocale.isKhmer && link.labelKh.isNotBlank()) link.labelKh else link.label
                        val isAba = link.id.equals("aba", ignoreCase = true)
                        if (isAba) {
                            Button(
                                enabled = !confirming,
                                onClick = {
                                    awaitingBankReturn = true
                                    openBankDeepLink(context, link)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                            ) {
                                Text(
                                    stringApp(R.string.business_khqr_open_bank, label),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        } else {
                            OutlinedButton(
                                enabled = !confirming,
                                onClick = {
                                    awaitingBankReturn = true
                                    openBankDeepLink(context, link)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text(stringApp(R.string.business_khqr_open_bank, label))
                            }
                        }
                    }
                    // Fallback if user scanned QR with another phone / without opening bank from here.
                    TextButton(
                        enabled = !confirming,
                        onClick = {
                            val id = pay.payment?.id ?: return@TextButton
                            confirmPayment(id, fromAuto = false)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringLocalized(R.string.business_khqr_confirm, R.string.business_khqr_confirm_kh))
                    }
                    TextButton(
                        onClick = {
                            checkout = null
                            awaitingBankReturn = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringLocalized(R.string.business_khqr_new, R.string.business_khqr_new_kh))
                    }
                }
            }
        }

        if (checkout == null && !paymentSuccess) {
            if (bakongEnabled) {
                Text(
                    stringLocalized(R.string.business_khqr_currency, R.string.business_khqr_currency_kh),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = payCurrency == "USD",
                        onClick = { payCurrency = "USD" },
                        label = {
                            Text(stringLocalized(R.string.business_khqr_usd, R.string.business_khqr_usd_kh))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                    FilterChip(
                        selected = payCurrency == "KHR",
                        onClick = { payCurrency = "KHR" },
                        label = {
                            Text(stringLocalized(R.string.business_khqr_khr, R.string.business_khqr_khr_kh))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            plans.forEach { plan ->
                val isBest = plan.id.equals("yearly", ignoreCase = true)
                val isPopular = plan.id.equals("semiannual", ignoreCase = true)
                val scheme = MaterialTheme.colorScheme
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isBest) scheme.primaryContainer.copy(alpha = 0.12f) else scheme.surface,
                    border = BorderStroke(
                        width = if (isBest || isPopular) 1.5.dp else 1.dp,
                        color = when {
                            isBest -> scheme.primaryContainer
                            isPopular -> scheme.tertiary.copy(alpha = 0.55f)
                            else -> scheme.outlineVariant.copy(alpha = 0.45f)
                        },
                    ),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                if (AppLocale.isKhmer && plan.nameKh.isNotBlank()) plan.nameKh else plan.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            when {
                                isBest -> {
                                    Text(
                                        stringLocalized(
                                            R.string.business_plan_best_value,
                                            R.string.business_plan_best_value_kh,
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(scheme.primaryContainer)
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        color = scheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                isPopular -> {
                                    Text(
                                        stringLocalized(
                                            R.string.business_plan_popular,
                                            R.string.business_plan_popular_kh,
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(scheme.tertiary.copy(alpha = 0.18f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        color = scheme.tertiary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        val khr = if (plan.priceKhr > 0) {
                            plan.priceKhr
                        } else {
                            (plan.priceUsd * usdToKhrRate).toInt().coerceAtLeast(1)
                        }
                        Text(
                            stringApp(
                                R.string.business_price_both,
                                String.format("%.2f", plan.priceUsd),
                                "%,d".format(khr),
                            ),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = scheme.primary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (AppLocale.isKhmer && plan.descriptionKh.isNotBlank()) {
                                plan.descriptionKh
                            } else {
                                plan.description
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
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
                            Text(
                                "• $benefit",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        Text(
                            stringApp(R.string.business_no_refund_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        if (bakongEnabled) {
                            Button(
                                enabled = payingId == null,
                                onClick = {
                                    scope.launch {
                                        payingId = plan.id
                                        error = null
                                        message = null
                                        paymentSuccess = false
                                        repo.createBakongPayment(plan.id, payCurrency)
                                            .onSuccess {
                                                payingId = null
                                                checkout = it
                                                if (it.autoVerify) bakongAutoVerify = true
                                                awaitingBankReturn = false
                                            }
                                            .onFailure {
                                                error = it.message
                                                payingId = null
                                            }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = scheme.primaryContainer,
                                    contentColor = scheme.onPrimaryContainer,
                                ),
                            ) {
                                Text(
                                    if (payingId == plan.id) {
                                        "…"
                                    } else {
                                        stringLocalized(
                                            R.string.business_pay_khqr,
                                            R.string.business_pay_khqr_kh,
                                        ) + if (payCurrency == "KHR") " (៛)" else " ($)"
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        OutlinedButton(
                            enabled = payingId == null,
                            onClick = {
                                scope.launch {
                                    payingId = plan.id
                                    error = null
                                    repo.sandboxPay(plan.id)
                                        .onSuccess {
                                            payingId = null
                                            finishSuccess(it.message)
                                        }
                                        .onFailure {
                                            error = it.message
                                            payingId = null
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(
                                if (payingId == plan.id && !bakongEnabled) {
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
        }

        if (!bakongEnabled && !paymentSuccess) {
            Text(
                bakongMessage
                    ?: stringLocalized(R.string.business_bakong_soon, R.string.business_bakong_soon_kh),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/** Known Play Store package IDs (server may be stale; keep local fallbacks). */

private fun bankPackagesFor(link: BankDeepLink): List<String> {
    val fromApi = link.androidPackage?.takeIf { it.isNotBlank() }
    val defaults = when (link.id.lowercase()) {
        "aba" -> listOf("com.paygo24.ibank")
        "acleda" -> listOf("com.domain.acledabankqr")
        "wing" -> listOf("com.wingmoney.wingpay")
        "bakong" -> listOf("jp.co.soramitsu.bakong")
        else -> emptyList()
    }
    return (listOfNotNull(fromApi) + defaults).distinct()
}

/**
 * ABA: deep-link with QR embedded (payway).
 * Other banks: no public payway scheme → launch app (user scans on-screen QR),
 * or open Play Store if not installed.
 */
private fun openBankDeepLink(context: Context, link: BankDeepLink) {
    val pm = context.packageManager
    val label = if (AppLocale.isKhmer && link.labelKh.isNotBlank()) link.labelKh else link.label
    val packages = bankPackagesFor(link)

    val scheme = link.scheme?.takeIf { it.isNotBlank() }
    if (scheme != null) {
        for (pkg in packages.ifEmpty { listOf(null) }) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (pkg != null) intent.setPackage(pkg)
                if (intent.resolveActivity(pm) != null || pkg == null) {
                    context.startActivity(intent)
                    return
                }
            } catch (_: Exception) {
                // try next
            }
        }
        // Scheme without package pin (some OEM ABA builds)

        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        } catch (_: Exception) {
            // fall through to launch / store
        }
    }

    for (pkg in packages) {
        val launch = pm.getLaunchIntentForPackage(pkg)
        if (launch != null) {
            try {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launch)
                Toast.makeText(
                    context,
                    context.stringApp(R.string.business_khqr_scan_in_bank, label),
                    Toast.LENGTH_LONG,
                ).show()
                return
            } catch (_: Exception) {
                // try next package
            }
        }
    }

    val storePkg = packages.firstOrNull()
    val market = storePkg?.let { "market://details?id=$it" }
    val https = link.playStoreUrl?.takeIf { it.isNotBlank() }
        ?: storePkg?.let { "https://play.google.com/store/apps/details?id=$it" }
    for (url in listOfNotNull(market, https)) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        } catch (_: Exception) {
            // try next
        }
    }

    Toast.makeText(
        context,
        context.stringApp(R.string.business_khqr_open_bank_fail, label),
        Toast.LENGTH_LONG,
    ).show()
}