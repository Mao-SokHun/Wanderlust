package com.example.wanderlust.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.wanderlust.R
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.data.NearbyDestination
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.destinationsNear
import com.example.wanderlust.data.formatDistanceKm
import com.example.wanderlust.data.resolvedGeo
import com.example.wanderlust.locale.localizedLocation
import com.example.wanderlust.locale.stringLocalized
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun NearbyMapSection(
    onDestinationClick: (DestinationCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val locationEnabled = SessionManager.userLocationEnabled
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        hasPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasPermission) {
            errorMessage = context.stringApp(R.string.nearby_permission_denied)
        }
    }

    LaunchedEffect(hasPermission, locationEnabled) {
        if (!locationEnabled || !hasPermission) return@LaunchedEffect
        isLoading = true
        errorMessage = null
        val location = fetchUserLocation(context)
        if (location != null) {
            userLatLng = location
            isLoading = false
        } else {
            isLoading = false
            errorMessage = context.stringApp(R.string.nearby_location_unavailable)
        }
    }

    val nearby = remember(userLatLng) {
        val pos = userLatLng ?: return@remember emptyList()
        GuestAccess.limitForGuest(destinationsNear(pos.latitude, pos.longitude))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            stringLocalized(R.string.nearby_you_title, R.string.nearby_you_title_kh),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            stringLocalized(R.string.nearby_you_subtitle, R.string.nearby_you_subtitle_kh),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
        )

        when {
            !locationEnabled -> {
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Text(
                        stringApp(R.string.nearby_disabled_settings),
                        Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            !hasPermission -> {
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            stringApp(R.string.nearby_need_permission),
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
                            Text(stringApp(R.string.nearby_enable_location))
                        }
                    }
                }
            }
            isLoading && userLatLng == null -> {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringApp(R.string.nearby_finding),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            errorMessage != null && userLatLng == null -> {
                StitchGhostCard(Modifier.fillMaxWidth()) {
                    Text(
                        errorMessage.orEmpty(),
                        Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            userLatLng != null -> {
                NearbyMapContent(
                    userLatLng = userLatLng!!,
                    nearby = nearby,
                    onDestinationClick = onDestinationClick,
                )
            }
        }
    }
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

@Composable
private fun NearbyMapContent(
    userLatLng: LatLng,
    nearby: List<NearbyDestination>,
    onDestinationClick: (DestinationCard) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 10f)
    }
    val youMarkerState = rememberMarkerState(position = userLatLng)
    LaunchedEffect(userLatLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 10f)
        youMarkerState.position = userLatLng
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState,
    ) {
        Marker(
            state = youMarkerState,
            title = stringApp(R.string.nearby_you_marker),
        )
        nearby.forEach { item ->
            key(item.destination.id) {
                val geo = item.destination.resolvedGeo()
                Marker(
                    state = rememberMarkerState(position = LatLng(geo.latitude, geo.longitude)),
                    title = item.destination.title,
                    snippet = formatDistanceKm(item.distanceKm),
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))
    nearby.forEach { item ->
        StitchGhostCard(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable { onDestinationClick(item.destination) },
        ) {
            Column(Modifier.padding(12.dp)) {
                DestinationTitleBlock(
                    item.destination,
                    titleStyle = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "${item.destination.localizedLocation()} • ${formatDistanceKm(item.distanceKm)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
