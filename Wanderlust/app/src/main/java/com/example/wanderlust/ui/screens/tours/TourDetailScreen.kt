package com.example.wanderlust.ui.screens.tours

import com.example.wanderlust.R

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.data.WanderlustImages
import com.example.wanderlust.data.GeoLocation
import com.example.wanderlust.data.destinationsNear
import com.example.wanderlust.data.formatDistanceKm
import com.example.wanderlust.data.geoForDestination
import com.example.wanderlust.locale.localizedCategory
import com.example.wanderlust.locale.localizedDescription
import com.example.wanderlust.locale.localizedLocation
import com.example.wanderlust.locale.localizedTitle
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.DestinationTitleBlock
import com.example.wanderlust.ui.components.RegisterToSaveDialog
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.TourPackageSections
import com.example.wanderlust.ui.components.TripDetailSections
import com.example.wanderlust.ui.components.RentalDetailSections
import com.example.wanderlust.viewmodel.TourDetailViewModel

@Composable
fun TourDetailScreen(
    destination: DestinationCard,
    onBack: () -> Unit,
    onSavePlace: () -> Unit,
    onOpenNearby: (DestinationCard) -> Unit,
    onSignIn: () -> Unit,
    onRegister: () -> Unit = onSignIn,
) {
    key(destination.id) {
        TourDetailContent(
            destination = destination,
            onBack = onBack,
            onSavePlace = onSavePlace,
            onOpenNearby = onOpenNearby,
            onSignIn = onSignIn,
            onRegister = onRegister,
            viewModel = viewModel(),
        )
    }
}

@Composable
private fun TourDetailContent(
    destination: DestinationCard,
    onBack: () -> Unit,
    onSavePlace: () -> Unit,
    onOpenNearby: (DestinationCard) -> Unit,
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
    viewModel: TourDetailViewModel,
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    var showRegisterToSave by remember { mutableStateOf(false) }
    val placeLabel = destination.localizedLocation()

    fun trySave() {
        if (GuestAccess.requiresAccountToSave()) {
            showRegisterToSave = true
        } else {
            viewModel.toggleSave(destination)
            onSavePlace()
        }
    }

    if (showRegisterToSave) {
        RegisterToSaveDialog(
            onDismiss = { showRegisterToSave = false },
            onRegister = onRegister,
            onSignIn = onSignIn,
        )
    }

    val location = remember(destination.id, destination.latitude, destination.longitude, placeLabel) {
        if (destination.latitude != null && destination.longitude != null) {
            GeoLocation(placeLabel, destination.latitude, destination.longitude)
        } else {
            geoForDestination(destination.title, destination.category)
        }
    }
    val gallery = remember(destination.id) {
        WanderlustImages.galleryForPlace(destination.id, destination.title, destination.category).take(3)
    }
    val nearby = remember(destination.id, location.latitude, location.longitude) {
        destinationsNear(
            latitude = location.latitude,
            longitude = location.longitude,
            limit = 4,
            excludeId = destination.id,
        )
    }

    LaunchedEffect(destination.id) {
        viewModel.loadSavedState(destination.id, destination.title)
        viewModel.initRating(destination)
    }

    val savedSnackText = stringLocalized(R.string.msg_saved, R.string.msg_saved_kh)
    val removedSnackText = stringLocalized(R.string.msg_removed, R.string.msg_removed_kh)
    val failedSnackText = stringLocalized(R.string.msg_save_failed, R.string.msg_save_failed_kh)
    val rateThanks = stringLocalized(R.string.rate_tour_thanks, R.string.rate_tour_thanks_kh)
    val rateSignIn = stringLocalized(R.string.rate_sign_in, R.string.rate_sign_in_kh)

    LaunchedEffect(viewModel.saveMessage) {
        val code = viewModel.saveMessage ?: return@LaunchedEffect
        val text = when (code) {
            "saved" -> savedSnackText
            "removed" -> removedSnackText
            else -> failedSnackText
        }
        snackbar.showSnackbar(text)
        viewModel.clearSaveMessage()
    }

    LaunchedEffect(viewModel.rateMessage) {
        val code = viewModel.rateMessage ?: return@LaunchedEffect
        val text = when (code) {
            "ok" -> rateThanks
            "signin" -> rateSignIn
            else -> failedSnackText
        }
        snackbar.showSnackbar(text)
        viewModel.clearRateMessage()
    }

    LaunchedEffect(destination.id) {
        viewModel.loadWeather(location.latitude, location.longitude)
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(Modifier.fillMaxWidth().height(220.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    gallery.forEach { url ->
                        AsyncImage(
                            url,
                            destination.title,
                            Modifier
                                .width(320.dp)
                                .height(220.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Black.copy(0.25f), Color.Black.copy(0.65f))),
                    ),
                )
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(
                        stringLocalized(R.string.tour_premium, R.string.tour_premium_kh),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                    Text(
                        destination.localizedTitle(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.height(16.dp))
                        Text(
                            " ${viewModel.displayRating} (${viewModel.displayRatingCount}) • ${destination.localizedLocation()}",
                            color = Color.White.copy(0.9f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(
                    stringLocalized(R.string.tour_experience_title, R.string.tour_experience_title_kh),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    destination.localizedDescription().ifEmpty {
                        stringLocalized(R.string.tour_default_desc, R.string.tour_default_desc_kh)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )

                destination.packageDetails?.let { pkg ->
                    TourPackageSections(
                        pkg = pkg,
                        priceLabel = destination.priceLabel,
                    )
                }
                destination.tripDetails?.let { trip ->
                    TripDetailSections(
                        trip = trip,
                        priceLabel = destination.priceLabel,
                    )
                }
                destination.rentalDetails?.let { rental ->
                    RentalDetailSections(
                        rental = rental,
                        priceLabel = destination.priceLabel,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TourTag(stringLocalized(R.string.tag_guides, R.string.tag_guides_kh))
                    TourTag(stringLocalized(R.string.tag_small_group, R.string.tag_small_group_kh))
                    TourTag(destination.localizedCategory())
                    when (destination.listingType) {
                        "TRIP" -> TourTag(stringApp(R.string.tours_filter_trips))
                        "RENTAL" -> TourTag(stringApp(R.string.tours_filter_rentals))
                        "VEHICLE" -> TourTag(
                            stringLocalized(
                                R.string.tours_filter_vehicles,
                                R.string.tours_filter_vehicles_kh,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                if (destination.id.matches(Regex("^\\d+$"))) {
                    StitchGhostCard(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                stringLocalized(R.string.rate_tour_title, R.string.rate_tour_title_kh),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..5).forEach { star ->
                                    IconButton(onClick = { viewModel.selectStars(star) }) {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = "$star",
                                            tint = if (star <= viewModel.myRating) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline
                                            },
                                        )
                                    }
                                }
                            }
                            if (com.example.wanderlust.data.SessionManager.isLoggedIn()) {
                                Button(
                                    enabled = !viewModel.ratingBusy && viewModel.myRating > 0,
                                    onClick = { viewModel.submitRating(destination.id) },
                                ) {
                                    Text(
                                        stringLocalized(
                                            R.string.rate_tour_submit,
                                            R.string.rate_tour_submit_kh,
                                        ),
                                    )
                                }
                            } else {
                                Button(onClick = onSignIn) {
                                    Text(
                                        stringLocalized(
                                            R.string.rate_sign_in,
                                            R.string.rate_sign_in_kh,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringApp(R.string.map_location), fontWeight = FontWeight.SemiBold)
                        Text(location.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${stringLocalized(R.string.weather_label, R.string.weather_label_kh)} ${viewModel.weatherText}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = {
                                val uri = Uri.parse(
                                    "geo:${location.latitude},${location.longitude}?q=${Uri.encode(location.name)}",
                                )
                                val mapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                val launch = if (mapsIntent.resolveActivity(context.packageManager) != null) {
                                    mapsIntent
                                } else {
                                    Intent(Intent.ACTION_VIEW, uri)
                                }
                                runCatching { context.startActivity(launch) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringLocalized(R.string.open_maps, R.string.open_maps_kh))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                stringLocalized(R.string.tour_category_label, R.string.tour_category_label_kh),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                destination.localizedCategory(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Button(onClick = { trySave() }) {
                            Text(
                                if (GuestAccess.requiresAccountToSave()) {
                                    stringLocalized(
                                        R.string.guest_save_alert_title,
                                        R.string.guest_save_alert_title_kh,
                                    )
                                } else {
                                    stringLocalized(R.string.btn_save_place, R.string.btn_save_place_kh)
                                },
                            )
                        }
                    }
                }
                Text(
                    stringLocalized(R.string.tour_free_hint, R.string.tour_free_hint_kh),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Spacer(Modifier.height(16.dp))
                Text(stringApp(R.string.nearby_title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                nearby.forEach { item ->
                    val suggestion = item.destination
                    StitchGhostCard(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { onOpenNearby(suggestion) },
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            DestinationTitleBlock(
                                suggestion,
                                titleStyle = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                "${suggestion.localizedLocation()} • ${formatDistanceKm(item.distanceKm)} • ★ ${suggestion.rating}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            IconButton(onClick = { trySave() }) {
                Icon(
                    if (viewModel.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (viewModel.isSaved) MaterialTheme.colorScheme.primary else Color.White,
                )
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}

@Composable
private fun TourTag(label: String) {
    Text(
        label,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelSmall,
    )
}
