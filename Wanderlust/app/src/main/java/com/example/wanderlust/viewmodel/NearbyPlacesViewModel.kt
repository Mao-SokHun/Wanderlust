package com.example.wanderlust.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlust.data.RecentNearbyPlace
import com.example.wanderlust.data.RecentPlacesStore
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.CustomPlaceInput
import com.example.wanderlust.data.model.NearbyMoment
import com.example.wanderlust.data.model.NearbyPlace
import com.example.wanderlust.data.model.NearbyPlaceCategories
import com.example.wanderlust.data.model.NearbySortMode
import com.example.wanderlust.data.repository.FavoriteRepository
import com.example.wanderlust.data.repository.PlacesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class NearbyPlacesUiState(
    val hasPermission: Boolean = false,
    val locationEnabledInSettings: Boolean = true,
    val userLatLng: LatLng? = null,
    val places: List<NearbyPlace> = emptyList(),
    val compareTop: List<NearbyPlace> = emptyList(),
    val bestPickId: String? = null,
    val selectedCategory: String? = null,
    val activeMomentId: String? = null,
    val searchQuery: String = "",
    val radiusMeters: Int = 1000,
    val sortMode: NearbySortMode = NearbySortMode.Nearest,
    val openNowOnly: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLoadingPlaces: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    /** Google place ids already in Saved list. */
    val savedPlaceIds: Set<String> = emptySet(),
    val recentPlaces: List<RecentNearbyPlace> = emptyList(),
    /** Bumps when results refresh so UI can scroll to the map/list. */
    val resultsVersion: Int = 0,
    val locationRetryKey: Int = 0,
)

class NearbyPlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlacesRepository(application)
    private val favoriteRepository = FavoriteRepository()
    private var searchJob: Job? = null
    private var debounceJob: Job? = null

    var uiState by mutableStateOf(
        NearbyPlacesUiState(
            locationEnabledInSettings = SessionManager.userLocationEnabled,
            recentPlaces = RecentPlacesStore.load(application),
        ),
    )
        private set

    val quickNeedLabels = NearbyPlaceCategories.quickNeeds.map { it.label }
    val moreCategoryLabels = NearbyPlaceCategories.moreCategories.map { it.label }
    val allCategoryLabels = NearbyPlaceCategories.all.map { it.label }
    val radiusOptions = NearbyPlaceCategories.radiusOptionsMeters
    val moments: List<NearbyMoment> = NearbyPlaceCategories.momentsForHour()

    fun onPermissionResult(granted: Boolean) {
        uiState = uiState.copy(
            hasPermission = granted,
            errorMessage = if (!granted) {
                "Location permission is required to show places near you."
            } else {
                null
            },
        )
    }

    fun setPermissionAlreadyGranted(granted: Boolean) {
        uiState = uiState.copy(hasPermission = granted)
    }

    fun refreshSettingsFlag() {
        uiState = uiState.copy(locationEnabledInSettings = SessionManager.userLocationEnabled)
    }

    fun applyMoment(moment: NearbyMoment) {
        debounceJob?.cancel()
        uiState = uiState.copy(
            activeMomentId = moment.id,
            selectedCategory = moment.categoryLabel,
            searchQuery = moment.keyword,
            openNowOnly = moment.openNowOnly,
        )
        loadPlaces()
    }

    fun onCategorySelect(label: String?) {
        debounceJob?.cancel()
        uiState = uiState.copy(
            selectedCategory = label,
            searchQuery = "",
            activeMomentId = null,
        )
        loadPlaces()
    }

    fun quickSelect(label: String) {
        debounceJob?.cancel()
        uiState = uiState.copy(
            selectedCategory = label,
            searchQuery = "",
            activeMomentId = null,
        )
        loadPlaces()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(
            searchQuery = query,
            selectedCategory = if (query.isNotBlank()) null else uiState.selectedCategory,
            activeMomentId = if (query.isNotBlank()) null else uiState.activeMomentId,
        )
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadPlaces()
        }
    }

    fun clearSearch() {
        debounceJob?.cancel()
        uiState = uiState.copy(searchQuery = "")
        loadPlaces()
    }

    fun submitSearch() {
        debounceJob?.cancel()
        if (uiState.searchQuery.isNotBlank()) {
            uiState = uiState.copy(selectedCategory = null, activeMomentId = null)
        }
        loadPlaces()
    }

    fun onRadiusChange(meters: Int) {
        if (meters == uiState.radiusMeters) return
        uiState = uiState.copy(radiusMeters = meters)
        loadPlaces()
    }

    fun onSortModeChange(mode: NearbySortMode) {
        val resorted = when (mode) {
            NearbySortMode.Nearest -> uiState.places.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
            NearbySortMode.TopRated -> uiState.places.sortedWith(
                compareByDescending<NearbyPlace> { it.rating ?: -1.0 }
                    .thenByDescending { it.userRatingsTotal ?: 0 }
                    .thenBy { it.distanceKm ?: Double.MAX_VALUE },
            )
        }
        uiState = uiState.copy(
            sortMode = mode,
            places = resorted,
            compareTop = resorted.take(3),
            bestPickId = NearbyPlaceCategories.pickBest(resorted)?.id,
        )
    }

    fun onOpenNowOnlyChange(enabled: Boolean) {
        uiState = uiState.copy(
            openNowOnly = enabled,
            activeMomentId = if (enabled) "open_now" else uiState.activeMomentId?.takeIf { it != "open_now" },
        )
        loadPlaces()
    }

    fun onUserLocation(latLng: LatLng) {
        uiState = uiState.copy(
            userLatLng = latLng,
            isLoadingLocation = false,
            errorMessage = null,
        )
        loadPlaces()
    }

    fun onLocationLoading() {
        uiState = uiState.copy(isLoadingLocation = true, errorMessage = null)
    }

    fun onLocationFailed(message: String) {
        uiState = uiState.copy(isLoadingLocation = false, errorMessage = message)
    }

    fun retryLocation() {
        uiState = uiState.copy(
            userLatLng = null,
            errorMessage = null,
            isLoadingLocation = true,
            locationRetryKey = uiState.locationRetryKey + 1,
        )
    }

    fun clearSnackbar() {
        uiState = uiState.copy(snackbarMessage = null)
    }

    fun rememberRecent(place: NearbyPlace) {
        val recent = RecentNearbyPlace(
            id = place.id,
            name = place.name,
            address = place.address,
            latitude = place.latitude,
            longitude = place.longitude,
            rating = place.rating,
            primaryType = place.primaryType,
            phoneNumber = place.phoneNumber,
            hasPhoto = place.hasPhoto,
        )
        RecentPlacesStore.remember(getApplication(), recent)
        uiState = uiState.copy(recentPlaces = RecentPlacesStore.load(getApplication()))
    }

    fun recentAsNearby(recent: RecentNearbyPlace): NearbyPlace =
        NearbyPlace(
            id = recent.id,
            name = recent.name,
            address = recent.address,
            latitude = recent.latitude,
            longitude = recent.longitude,
            rating = recent.rating,
            primaryType = recent.primaryType,
            phoneNumber = recent.phoneNumber,
            hasPhoto = recent.hasPhoto,
            distanceKm = uiState.userLatLng?.let { pos ->
                com.example.wanderlust.data.distanceKm(
                    pos.latitude,
                    pos.longitude,
                    recent.latitude,
                    recent.longitude,
                )
            },
        )

    suspend fun loadPlacePhoto(placeId: String): android.graphics.Bitmap? =
        repository.fetchPlacePhoto(placeId)

    fun savePlace(place: NearbyPlace) {
        if (!SessionManager.isLoggedIn()) {
            uiState = uiState.copy(snackbarMessage = "sign_in_required")
            return
        }
        if (place.id in uiState.savedPlaceIds) {
            uiState = uiState.copy(snackbarMessage = "saved")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            favoriteRepository.addCustomPlace(
                CustomPlaceInput(
                    title = place.name,
                    location = place.address.ifBlank { place.primaryType.ifBlank { "Nearby place" } },
                    notes = buildString {
                        place.primaryType.takeIf { it.isNotBlank() }?.let { append(it) }
                        place.rating?.let {
                            if (isNotEmpty()) append(" • ")
                            append("★ $it")
                        }
                    },
                    latitude = place.latitude,
                    longitude = place.longitude,
                    mapsLink = "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}",
                    preferredId = place.id,
                ),
            ).onSuccess {
                uiState = uiState.copy(
                    isSaving = false,
                    snackbarMessage = "saved",
                    savedPlaceIds = uiState.savedPlaceIds + place.id,
                )
            }.onFailure { err ->
                uiState = uiState.copy(
                    isSaving = false,
                    snackbarMessage = err.message ?: "save_failed",
                )
            }
        }
    }

    fun refreshSavedPlaceMarks() {
        if (!SessionManager.isLoggedIn()) {
            uiState = uiState.copy(savedPlaceIds = emptySet())
            return
        }
        viewModelScope.launch {
            val savedIds = favoriteRepository.savedGooglePlaceIds()
            val savedTitles = favoriteRepository.savedTitles()
            val matched = uiState.places
                .filter { it.id in savedIds || it.name.trim().lowercase() in savedTitles }
                .map { it.id }
                .toSet()
            uiState = uiState.copy(savedPlaceIds = savedIds + matched)
        }
    }

    fun loadPlaces() {
        val pos = uiState.userLatLng ?: return
        val type = NearbyPlaceCategories.all
            .firstOrNull { it.label == uiState.selectedCategory }
            ?.googleType
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            uiState = uiState.copy(isLoadingPlaces = true, errorMessage = null)
            repository.searchNearby(
                latitude = pos.latitude,
                longitude = pos.longitude,
                googleType = type,
                keyword = uiState.searchQuery,
                radiusMeters = uiState.radiusMeters.toDouble(),
            ).onSuccess { places ->
                val prepared = applyFiltersAndSort(places)
                val best = NearbyPlaceCategories.pickBest(prepared)
                uiState = uiState.copy(
                    places = prepared,
                    compareTop = prepared.take(3),
                    bestPickId = best?.id,
                    isLoadingPlaces = false,
                    resultsVersion = uiState.resultsVersion + 1,
                    errorMessage = if (prepared.isEmpty()) {
                        "No places found nearby. Try another category, larger radius, or keyword."
                    } else {
                        null
                    },
                )
                refreshSavedPlaceMarks()
            }.onFailure { err ->
                uiState = uiState.copy(
                    places = emptyList(),
                    compareTop = emptyList(),
                    bestPickId = null,
                    isLoadingPlaces = false,
                    resultsVersion = uiState.resultsVersion + 1,
                    errorMessage = err.message
                        ?: "Could not load nearby places. Enable Places API (New) for your key.",
                )
            }
        }
    }

    private fun applyFiltersAndSort(source: List<NearbyPlace>): List<NearbyPlace> {
        var list = source
        if (uiState.openNowOnly) {
            list = list.filter { it.openNow != false }
            val knownOpen = list.filter { it.openNow == true }
            if (knownOpen.isNotEmpty()) {
                list = knownOpen
            }
        }
        return when (uiState.sortMode) {
            NearbySortMode.Nearest -> list.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
            NearbySortMode.TopRated -> list.sortedWith(
                compareByDescending<NearbyPlace> { it.rating ?: -1.0 }
                    .thenByDescending { it.userRatingsTotal ?: 0 }
                    .thenBy { it.distanceKm ?: Double.MAX_VALUE },
            )
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 220L
    }
}
