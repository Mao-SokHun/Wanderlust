package com.example.wanderlust.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.R
import com.example.wanderlust.data.RecentNearbyPlace
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.formatDistanceKmValue
import com.example.wanderlust.data.model.NearbyMoment
import com.example.wanderlust.data.model.NearbyPlace
import com.example.wanderlust.data.model.NearbyPlaceCategories
import com.example.wanderlust.data.model.NearbySortMode
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.viewmodel.NearbyPlacesViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NearbyPlacesExplorer(
    viewModel: NearbyPlacesViewModel = viewModel(),
    onSignIn: (() -> Unit)? = null,
    onPlaceSaved: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val mapBringIntoView = remember { BringIntoViewRequester() }
    var permissionPromptStarted by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<NearbyPlace?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val savedText = stringLocalized(R.string.msg_saved, R.string.msg_saved_kh)
    val signInRequired = stringApp(R.string.nearby_sign_in_to_save)
    val saveFailed = stringLocalized(R.string.msg_save_failed, R.string.msg_save_failed_kh)
    val copiedText = stringApp(R.string.nearby_copied)

    fun openPlaceDetails(place: NearbyPlace) {
        viewModel.rememberRecent(place)
        selectedPlace = place
    }

    LaunchedEffect(state.snackbarMessage) {
        val code = state.snackbarMessage ?: return@LaunchedEffect
        when (code) {
            "saved" -> {
                snackbar.showSnackbar(savedText)
                onPlaceSaved?.invoke()
            }
            "sign_in_required" -> {
                snackbar.showSnackbar(signInRequired)
                onSignIn?.invoke()
            }
            "save_failed" -> snackbar.showSnackbar(saveFailed)
            else -> snackbar.showSnackbar(code)
        }
        viewModel.clearSnackbar()
    }

    LaunchedEffect(state.resultsVersion) {
        if (state.resultsVersion > 0 && state.userLatLng != null) {
            mapBringIntoView.bringIntoView()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val ok = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onPermissionResult(ok)
    }

    // Ask for location as soon as Home opens — no Enable button.
    LaunchedEffect(Unit) {
        viewModel.refreshSettingsFlag()
        val granted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        viewModel.setPermissionAlreadyGranted(granted)
        if (!granted && SessionManager.userLocationEnabled) {
            permissionPromptStarted = true
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    LaunchedEffect(state.hasPermission, state.locationEnabledInSettings, state.locationRetryKey) {
        if (!state.locationEnabledInSettings || !state.hasPermission) return@LaunchedEffect
        if (state.userLatLng != null) return@LaunchedEffect
        viewModel.onLocationLoading()
        val location = fetchUserLocation(context)
        if (location != null) {
            viewModel.onUserLocation(location)
        } else {
            viewModel.onLocationFailed(context.stringApp(R.string.nearby_location_unavailable))
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        when {
            !state.locationEnabledInSettings -> {
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            stringApp(R.string.nearby_disabled_settings),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(onClick = { openAppSettings(context) }) {
                            Text(stringApp(R.string.nearby_open_settings))
                        }
                    }
                }
            }
            !state.hasPermission -> {
                if (state.errorMessage != null) {
                    StitchGhostCard(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                stringApp(R.string.nearby_permission_denied),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                        ),
                                    )
                                },
                            ) {
                                Text(stringApp(R.string.nearby_try_permission_again))
                            }
                            TextButton(onClick = { openAppSettings(context) }) {
                                Text(stringApp(R.string.nearby_open_settings))
                            }
                        }
                    }
                } else {
                    LoadingBlock(stringApp(R.string.nearby_requesting_permission))
                }
            }
            state.isLoadingLocation && state.userLatLng == null -> {
                LoadingBlock(stringApp(R.string.nearby_finding))
            }
            state.errorMessage != null && state.userLatLng == null -> {
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            state.errorMessage.orEmpty(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = { viewModel.retryLocation() },
                        ) {
                            Text(stringApp(R.string.btn_retry))
                        }
                    }
                }
            }
            state.userLatLng != null -> {
                RightNowMomentsRow(
                    moments = viewModel.moments,
                    activeMomentId = state.activeMomentId,
                    onSelect = viewModel::applyMoment,
                )
                Spacer(Modifier.height(8.dp))
                CompactCategoryChips(
                    selected = state.selectedCategory,
                    quickLabels = viewModel.quickNeedLabels,
                    moreLabels = viewModel.moreCategoryLabels,
                    onQuickSelect = viewModel::quickSelect,
                    onMoreSelect = viewModel::onCategorySelect,
                )
                Spacer(Modifier.height(8.dp))
                CompactFilterRow(
                    radiusMeters = state.radiusMeters,
                    radiusOptions = viewModel.radiusOptions,
                    sortMode = state.sortMode,
                    openNowOnly = state.openNowOnly,
                    onRadiusChange = viewModel::onRadiusChange,
                    onSortModeChange = viewModel::onSortModeChange,
                    onOpenNowOnlyChange = viewModel::onOpenNowOnlyChange,
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier.bringIntoViewRequester(mapBringIntoView),
                ) {
                    NearbyPlacesMap(
                        userLatLng = state.userLatLng!!,
                        places = state.places,
                        onPlaceClick = { openPlaceDetails(it) },
                    )
                    Spacer(Modifier.height(8.dp))
                    when {
                        state.isLoadingPlaces -> LoadingBlock(stringApp(R.string.nearby_loading_places))
                        state.errorMessage != null && state.places.isEmpty() -> {
                            Text(
                                state.errorMessage.orEmpty(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        else -> {
                            if (state.compareTop.isNotEmpty()) {
                                CompareTopStrip(
                                    places = state.compareTop,
                                    bestPickId = state.bestPickId,
                                    loadPhoto = { id -> viewModel.loadPlacePhoto(id) },
                                    onSelect = { openPlaceDetails(it) },
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                            Text(
                                stringApp(R.string.nearby_results_count, state.places.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            state.places.forEach { place ->
                                NearbyPlaceActionCard(
                                    place = place,
                                    isBestPick = place.id == state.bestPickId,
                                    isSaved = place.id in state.savedPlaceIds,
                                    isSaving = state.isSaving,
                                    loadPhoto = { id -> viewModel.loadPlacePhoto(id) },
                                    onOpenDetails = { openPlaceDetails(place) },
                                    onDirections = { openDirections(context, place) },
                                    onCall = place.phoneNumber?.let { number ->
                                        { openCall(context, number) }
                                    },
                                    onSave = { viewModel.savePlace(place) },
                                )
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.padding(top = 8.dp))
    }

    selectedPlace?.let { place ->
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null },
            sheetState = detailSheetState,
        ) {
            NearbyPlaceDetailSheet(
                place = place,
                isBestPick = place.id == state.bestPickId,
                isSaved = place.id in state.savedPlaceIds,
                isSaving = state.isSaving,
                loadPhoto = { id -> viewModel.loadPlacePhoto(id) },
                onDirections = { openDirections(context, place) },
                onCall = place.phoneNumber?.let { number ->
                    { openCall(context, number) }
                },
                onSave = { viewModel.savePlace(place) },
                onCopyAddress = {
                    val text = place.address.ifBlank { place.name }
                    clipboard.setText(AnnotatedString(text))
                    scope.launch { snackbar.showSnackbar(copiedText) }
                },
                onShare = { sharePlace(context, place) },
                onClose = { selectedPlace = null },
            )
        }
    }
}

@Composable
private fun RightNowMomentsRow(
    moments: List<NearbyMoment>,
    activeMomentId: String?,
    onSelect: (NearbyMoment) -> Unit,
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            stringApp(R.string.nearby_right_now),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            moments.forEach { moment ->
                FilterChip(
                    selected = activeMomentId == moment.id,
                    onClick = { onSelect(moment) },
                    label = { Text(momentLabel(moment.id)) },
                    colors = chipColors,
                )
            }
        }
    }
}

@Composable
private fun momentLabel(momentId: String): String = stringApp(
    when (momentId) {
        "morning_cafe" -> R.string.nearby_moment_morning_cafe
        "morning_bakery" -> R.string.nearby_moment_morning_bakery
        "midday_food" -> R.string.nearby_moment_midday_food
        "midday_atm" -> R.string.nearby_moment_midday_atm
        "evening_food" -> R.string.nearby_moment_evening_food
        "evening_pharmacy" -> R.string.nearby_moment_evening_pharmacy
        "late_convenience" -> R.string.nearby_moment_late_convenience
        "late_gas" -> R.string.nearby_moment_late_gas
        "open_now" -> R.string.nearby_moment_open_now
        "emergency" -> R.string.nearby_moment_emergency
        else -> R.string.nearby_right_now
    },
)

@Composable
private fun RecentPlacesRow(
    recent: List<RecentNearbyPlace>,
    onSelect: (RecentNearbyPlace) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            stringApp(R.string.nearby_recent),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            recent.forEach { item ->
                FilterChip(
                    selected = false,
                    onClick = { onSelect(item) },
                    label = {
                        Text(
                            item.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Place, null, Modifier.size(16.dp))
                    },
                )
            }
        }
    }
}

@Composable
private fun CompareTopStrip(
    places: List<NearbyPlace>,
    bestPickId: String?,
    loadPhoto: suspend (String) -> Bitmap?,
    onSelect: (NearbyPlace) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            stringApp(R.string.nearby_compare_title),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            places.forEach { place ->
                ComparePlaceChip(
                    place = place,
                    isBest = place.id == bestPickId,
                    loadPhoto = loadPhoto,
                    onClick = { onSelect(place) },
                )
            }
        }
    }
}

@Composable
private fun ComparePlaceChip(
    place: NearbyPlace,
    isBest: Boolean,
    loadPhoto: suspend (String) -> Bitmap?,
    onClick: () -> Unit,
) {
    var photo by remember(place.id) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(place.id, place.hasPhoto) {
        if (place.hasPhoto) photo = loadPhoto(place.id)
    }
    val shape = RoundedCornerShape(14.dp)
    StitchGhostCard(
        Modifier
            .width(148.dp)
            .then(
                if (isBest) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isBest) {
                Text(
                    stringApp(R.string.nearby_best_pick),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (photo != null) {
                Image(
                    bitmap = photo!!.asImageBitmap(),
                    contentDescription = place.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                place.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                place.distanceKm?.let { km ->
                    Text(
                        stringApp(R.string.nearby_distance_km, formatDistanceKmValue(km)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                place.rating?.let {
                    Text("★ $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            when (place.openNow) {
                true -> Text(
                    stringApp(R.string.nearby_status_open),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                false -> Text(
                    stringApp(R.string.nearby_status_closed),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
                null -> Unit
            }
        }
    }
}


@Composable
private fun localizedNearbyCategory(label: String): String = when (label) {
    "Cafe" -> stringApp(R.string.nearby_cat_cafe)
    "Restaurant" -> stringApp(R.string.nearby_cat_restaurant)
    "ATM" -> stringApp(R.string.nearby_cat_atm)
    "Pharmacy" -> stringApp(R.string.nearby_cat_pharmacy)
    "Gas" -> stringApp(R.string.nearby_cat_gas)
    "Store" -> stringApp(R.string.nearby_cat_store)
    "Supermarket" -> stringApp(R.string.nearby_cat_supermarket)
    "Hotel" -> stringApp(R.string.nearby_cat_hotel)
    "Hospital" -> stringApp(R.string.nearby_cat_hospital)
    "Bank" -> stringApp(R.string.nearby_cat_bank)
    "Park" -> stringApp(R.string.nearby_cat_park)
    "Gym" -> stringApp(R.string.nearby_cat_gym)
    "Bakery" -> stringApp(R.string.nearby_cat_bakery)
    "Bar" -> stringApp(R.string.nearby_cat_bar)
    else -> label
}
@Composable
private fun CompactCategoryChips(
    selected: String?,
    quickLabels: List<String>,
    moreLabels: List<String>,
    onQuickSelect: (String) -> Unit,
    onMoreSelect: (String?) -> Unit,
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    )
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onMoreSelect(null) },
            label = { Text(stringApp(R.string.filter_all)) },
            colors = chipColors,
        )
        quickLabels.forEach { label ->
            FilterChip(
                selected = selected == label,
                onClick = { onQuickSelect(label) },
                leadingIcon = {
                    Icon(iconForQuickNeed(label), null, Modifier.size(16.dp))
                },
                label = { Text(localizedNearbyCategory(label)) },
                colors = chipColors,
            )
        }
        moreLabels.forEach { label ->
            FilterChip(
                selected = selected == label,
                onClick = { onMoreSelect(if (selected == label) null else label) },
                label = { Text(localizedNearbyCategory(label)) },
                colors = chipColors,
            )
        }
    }
}

@Composable
private fun CompactFilterRow(
    radiusMeters: Int,
    radiusOptions: List<Int>,
    sortMode: NearbySortMode,
    openNowOnly: Boolean,
    onRadiusChange: (Int) -> Unit,
    onSortModeChange: (NearbySortMode) -> Unit,
    onOpenNowOnlyChange: (Boolean) -> Unit,
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    )
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        radiusOptions.forEach { meters ->
            FilterChip(
                selected = radiusMeters == meters,
                onClick = { onRadiusChange(meters) },
                label = {
                    Text(
                        when (meters) {
                            500 -> stringApp(R.string.nearby_radius_500)
                            1000 -> stringApp(R.string.nearby_radius_1000)
                            2000 -> stringApp(R.string.nearby_radius_2000)
                            5000 -> stringApp(R.string.nearby_radius_5000)
                            10000 -> stringApp(R.string.nearby_radius_10000)
                            else -> "${meters / 1000} km"
                        },
                    )
                },
                colors = chipColors,
            )
        }
        FilterChip(
            selected = sortMode == NearbySortMode.Nearest,
            onClick = { onSortModeChange(NearbySortMode.Nearest) },
            label = { Text(stringApp(R.string.nearby_sort_nearest)) },
            colors = chipColors,
        )
        FilterChip(
            selected = sortMode == NearbySortMode.TopRated,
            onClick = { onSortModeChange(NearbySortMode.TopRated) },
            label = { Text(stringApp(R.string.nearby_sort_top_rated)) },
            colors = chipColors,
        )
        FilterChip(
            selected = openNowOnly,
            onClick = { onOpenNowOnlyChange(!openNowOnly) },
            label = { Text(stringApp(R.string.nearby_open_now)) },
            colors = chipColors,
        )
    }
}

private fun iconForQuickNeed(label: String): ImageVector = when (label) {
    "Cafe" -> Icons.Default.LocalCafe
    "Restaurant" -> Icons.Default.Restaurant
    "ATM" -> Icons.Default.AccountBalance
    "Pharmacy" -> Icons.Default.LocalPharmacy
    "Gas" -> Icons.Default.LocalGasStation
    "Store" -> Icons.Default.Store
    else -> Icons.Default.LocalHospital
}

@Composable
private fun LoadingBlock(label: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun NearbyPlacesMap(
    userLatLng: LatLng,
    places: List<NearbyPlace>,
    onPlaceClick: (NearbyPlace) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 14.5f)
    }
    val youMarkerState = rememberMarkerState(position = userLatLng)
    LaunchedEffect(userLatLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 14.5f)
        youMarkerState.position = userLatLng
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState,
    ) {
        Marker(
            state = youMarkerState,
            title = stringApp(R.string.nearby_you_marker),
        )
        places.forEach { place ->
            key(place.id) {
                Marker(
                    state = rememberMarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name,
                    snippet = place.primaryType.ifBlank { place.address },
                    onClick = {
                        onPlaceClick(place)
                        true
                    },
                )
            }
        }
    }
}

@Composable
private fun NearbyPlaceActionCard(
    place: NearbyPlace,
    isBestPick: Boolean,
    isSaved: Boolean,
    isSaving: Boolean,
    loadPhoto: suspend (String) -> Bitmap?,
    onOpenDetails: () -> Unit,
    onDirections: () -> Unit,
    onCall: (() -> Unit)?,
    onSave: () -> Unit,
) {
    var photo by remember(place.id) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(place.id, place.hasPhoto) {
        if (place.hasPhoto) {
            photo = loadPhoto(place.id)
        }
    }

    val shape = RoundedCornerShape(12.dp)
    StitchGhostCard(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .then(
                if (isBestPick) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onOpenDetails),
    ) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (photo != null) {
                Image(
                    bitmap = photo!!.asImageBitmap(),
                    contentDescription = place.name,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                StitchGhostCard(Modifier.size(84.dp)) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (isBestPick) {
                    Text(
                        stringApp(R.string.nearby_best_pick),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        bestPickReasonText(place),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
                Text(
                    place.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (place.primaryType.isNotBlank()) {
                        Text(
                            place.primaryType.replace('_', ' '),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    when (place.openNow) {
                        true -> Text(
                            stringApp(R.string.nearby_status_open),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        false -> Text(
                            stringApp(R.string.nearby_status_closed),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                        null -> Unit
                    }
                }
                val distanceLabel = place.distanceKm?.let {
                    stringApp(R.string.nearby_distance_km, formatDistanceKmValue(it))
                }
                val meta = buildString {
                    distanceLabel?.let { append(it) }
                    if (place.rating != null) {
                        if (isNotEmpty()) append(" • ")
                        append("★ ${place.rating}")
                        place.userRatingsTotal?.let { append(" ($it)") }
                    }
                }
                if (meta.isNotBlank()) {
                    Text(meta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                if (place.address.isNotBlank()) {
                    Text(
                        place.address,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    stringApp(R.string.nearby_tap_for_details),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDirections) {
                        Icon(Icons.Default.Directions, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(stringApp(R.string.nearby_directions), style = MaterialTheme.typography.labelMedium)
                    }
                    if (onCall != null) {
                        TextButton(onClick = onCall) {
                            Icon(Icons.Default.Call, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(2.dp))
                            Text(stringApp(R.string.nearby_call), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    TextButton(
                        onClick = onSave,
                        enabled = !isSaving && !isSaved,
                    ) {
                        Icon(
                            if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            Modifier.size(16.dp),
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            stringApp(if (isSaved) R.string.nearby_saved else R.string.nearby_save),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun bestPickReasonText(place: NearbyPlace): String {
    val parts = NearbyPlaceCategories.bestPickReasonKeyParts(place)
    val rating = parts.rating?.let { String.format("%.1f", it) } ?: "—"
    val distance = place.distanceKm?.let {
        stringApp(R.string.nearby_distance_km, formatDistanceKmValue(it))
    } ?: stringApp(R.string.nearby_you_marker)
    return when (parts.openKey) {
        "open" -> stringApp(R.string.nearby_best_reason_open, rating, distance)
        "closed" -> stringApp(R.string.nearby_best_reason_closed, rating, distance)
        else -> stringApp(R.string.nearby_best_reason_nearby, rating, distance)
    }
}

@Composable
private fun NearbyPlaceDetailSheet(
    place: NearbyPlace,
    isBestPick: Boolean,
    isSaved: Boolean,
    isSaving: Boolean,
    loadPhoto: suspend (String) -> Bitmap?,
    onDirections: () -> Unit,
    onCall: (() -> Unit)?,
    onSave: () -> Unit,
    onCopyAddress: () -> Unit,
    onShare: () -> Unit,
    onClose: () -> Unit,
) {
    var photo by remember(place.id) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(place.id, place.hasPhoto) {
        if (place.hasPhoto) {
            photo = loadPhoto(place.id)
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (photo != null) {
            Image(
                bitmap = photo!!.asImageBitmap(),
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
        }
        if (isBestPick) {
            Text(
                stringApp(R.string.nearby_best_pick),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                bestPickReasonText(place),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            place.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (place.primaryType.isNotBlank()) {
                Text(
                    place.primaryType.replace('_', ' '),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            when (place.openNow) {
                true -> Text(
                    stringApp(R.string.nearby_status_open),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                false -> Text(
                    stringApp(R.string.nearby_status_closed),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                null -> Unit
            }
        }
        val reviewsLabel = place.userRatingsTotal?.let {
            stringApp(R.string.nearby_reviews_count, it)
        }
        val distanceLabel = place.distanceKm?.let {
            stringApp(R.string.nearby_distance_km, formatDistanceKmValue(it))
        }
        val meta = buildString {
            distanceLabel?.let { append(it) }
            if (place.rating != null) {
                if (isNotEmpty()) append("  •  ")
                append("★ ${place.rating}")
                reviewsLabel?.let { append(" ($it)") }
            }
        }
        if (meta.isNotBlank()) {
            Text(meta, style = MaterialTheme.typography.bodyLarge)
        }
        if (place.address.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringApp(R.string.nearby_detail_address),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(place.address, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (!place.phoneNumber.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringApp(R.string.nearby_detail_phone),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(place.phoneNumber, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            stringApp(
                R.string.nearby_detail_coords,
                String.format("%.5f", place.latitude),
                String.format("%.5f", place.longitude),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onDirections, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Directions, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringApp(R.string.nearby_directions))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCopyAddress, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.ContentCopy, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringApp(R.string.nearby_copy_address))
            }
            OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Share, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringApp(R.string.nearby_share))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onCall != null) {
                Button(onClick = onCall, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Call, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringApp(R.string.nearby_call))
                }
            }
            Button(
                onClick = onSave,
                enabled = !isSaving && !isSaved,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    null,
                    Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(stringApp(if (isSaved) R.string.nearby_saved else R.string.nearby_save))
            }
        }
        TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringApp(R.string.nearby_detail_close))
        }
    }
}

private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )
    runCatching { context.startActivity(intent) }
}

private fun sharePlace(context: android.content.Context, place: NearbyPlace) {
    val mapsLink =
        "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}"
    val body = context.stringApp(
        R.string.nearby_share_text,
        place.name,
        place.address.ifBlank { place.primaryType.ifBlank { "Nearby" } },
        mapsLink,
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, place.name)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, context.stringApp(R.string.nearby_share)))
    }
}

private fun openDirections(context: android.content.Context, place: NearbyPlace) {
    val navUri = Uri.parse("google.navigation:q=${place.latitude},${place.longitude}")
    val navIntent = Intent(Intent.ACTION_VIEW, navUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    val fallback = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(
            "geo:${place.latitude},${place.longitude}?q=${Uri.encode("${place.name} ${place.address}")}",
        ),
    )
    val launch = if (navIntent.resolveActivity(context.packageManager) != null) navIntent else fallback
    runCatching { context.startActivity(launch) }
}

private fun openCall(context: android.content.Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.trim()}"))
    runCatching { context.startActivity(intent) }
}

@SuppressLint("MissingPermission")
private suspend fun fetchUserLocation(context: android.content.Context): LatLng? {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val last = suspendCancellableCoroutine { cont ->
        fused.lastLocation
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }
    if (last != null) {
        return LatLng(last.latitude, last.longitude)
    }
    val tokenSource = CancellationTokenSource()
    return suspendCancellableCoroutine { cont ->
        cont.invokeOnCancellation { tokenSource.cancel() }
        fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, tokenSource.token)
            .addOnSuccessListener { location ->
                cont.resume(
                    if (location != null) LatLng(location.latitude, location.longitude) else null,
                )
            }
            .addOnFailureListener { cont.resume(null) }
    }
}
