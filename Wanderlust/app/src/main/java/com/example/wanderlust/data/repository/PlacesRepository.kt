package com.example.wanderlust.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.distanceKm
import com.example.wanderlust.data.model.NearbyPlace
import com.example.wanderlust.data.model.NearbyPlaceCategories
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

class PlacesRepository(context: Context) {

    private val appContext = context.applicationContext
    private val photoMetadataByPlaceId = ConcurrentHashMap<String, PhotoMetadata>()
    private val photoBitmapCache = ConcurrentHashMap<String, Bitmap>()

    private val placesClient: PlacesClient by lazy {
        ensureInitialized()
        Places.createClient(appContext)
    }

    private fun ensureInitialized() {
        if (!Places.isInitialized()) {
            val key = BuildConfig.MAPS_API_KEY
            require(key.isNotBlank()) { "MAPS_API_KEY is missing in local.properties" }
            Places.initializeWithNewPlacesApiEnabled(appContext, key)
        }
    }

    private val placeFields: List<Place.Field> = buildList {
        add(Place.Field.ID)
        add(Place.Field.DISPLAY_NAME)
        add(Place.Field.FORMATTED_ADDRESS)
        add(Place.Field.LOCATION)
        add(Place.Field.RATING)
        add(Place.Field.TYPES)
        add(Place.Field.PRIMARY_TYPE)
        add(Place.Field.PRIMARY_TYPE_DISPLAY_NAME)
        addOptionalField("PHOTO_METADATAS")
        addOptionalField("USER_RATING_COUNT")
        addOptionalField("NATIONAL_PHONE_NUMBER")
        addOptionalField("INTERNATIONAL_PHONE_NUMBER")
        addOptionalField("OPEN_NOW")
        addOptionalField("CURRENT_OPENING_HOURS")
        addOptionalField("UTC_OFFSET")
    }

    private fun MutableList<Place.Field>.addOptionalField(name: String) {
        runCatching {
            add(Place.Field.valueOf(name))
        }
    }

    suspend fun searchNearby(
        latitude: Double,
        longitude: Double,
        googleType: String? = null,
        keyword: String = "",
        radiusMeters: Double = 1_000.0,
    ): Result<List<NearbyPlace>> = runCatching {
        ensureInitialized()
        val query = keyword.trim()
        val places = if (query.isNotEmpty()) {
            searchByText(latitude, longitude, query, radiusMeters)
        } else {
            searchNearbyTypes(latitude, longitude, googleType, radiusMeters)
        }
        places.map { it.toNearbyPlace(latitude, longitude) }
    }

    suspend fun fetchPlacePhoto(placeId: String, maxWidth: Int = 480): Bitmap? {
        photoBitmapCache[placeId]?.let { return it }
        val metadata = photoMetadataByPlaceId[placeId] ?: return null
        return runCatching {
            val request = FetchPhotoRequest.builder(metadata)
                .setMaxWidth(maxWidth)
                .setMaxHeight(maxWidth)
                .build()
            val bitmap = awaitTask { placesClient.fetchPhoto(request) }.bitmap
            photoBitmapCache[placeId] = bitmap
            bitmap
        }.getOrNull()
    }

    private suspend fun searchNearbyTypes(
        latitude: Double,
        longitude: Double,
        googleType: String?,
        radiusMeters: Double,
    ): List<Place> {
        val types = if (googleType.isNullOrBlank()) {
            NearbyPlaceCategories.broadTypes
        } else {
            listOf(googleType)
        }
        val center = LatLng(latitude, longitude)
        val bounds = CircularBounds.newInstance(center, radiusMeters)
        val request = SearchNearbyRequest.builder(bounds, placeFields)
            .setIncludedTypes(types)
            .setMaxResultCount(20)
            .setRankPreference(SearchNearbyRequest.RankPreference.DISTANCE)
            .build()
        return runCatching {
            awaitTask { placesClient.searchNearby(request) }.places
        }.getOrElse { error ->
            if (shouldFallbackToTextSearch(error)) {
                fallbackSearchByTextForTypes(latitude, longitude, types, radiusMeters)
            } else {
                throw error
            }
        }
    }

    private suspend fun searchByText(
        latitude: Double,
        longitude: Double,
        query: String,
        radiusMeters: Double,
    ): List<Place> {
        val center = LatLng(latitude, longitude)
        val bounds = CircularBounds.newInstance(center, radiusMeters)
        val request = SearchByTextRequest.builder(query, placeFields)
            .setLocationBias(bounds)
            .setMaxResultCount(20)
            .setRankPreference(SearchByTextRequest.RankPreference.DISTANCE)
            .build()
        return awaitTask { placesClient.searchByText(request) }.places
    }

    /** Falls back to text search when Nearby Search is blocked for the API key/project. */
    private suspend fun fallbackSearchByTextForTypes(
        latitude: Double,
        longitude: Double,
        types: List<String>,
        radiusMeters: Double,
    ): List<Place> {
        val queries = if (types.isEmpty()) {
            NearbyPlaceCategories.all.mapNotNull { it.googleType }.distinct()
        } else {
            types.distinct()
        }
        val merged = linkedMapOf<String, Place>()
        queries.forEach { type ->
            searchByText(latitude, longitude, type.replace('_', ' '), radiusMeters)
                .forEach { place ->
                    val key = place.id?.toString()
                        ?: "${place.location?.latitude},${place.location?.longitude}"
                    merged.putIfAbsent(key, place)
                }
        }
        return merged.values.take(20)
    }

    /** Detects the Google API error that blocks Places API Nearby Search requests. */
    private fun shouldFallbackToTextSearch(error: Throwable): Boolean {
        val message = error.message.orEmpty().lowercase()
        return "searchnearby" in message ||
            "places.searchnearby" in message ||
            "are blocked" in message ||
            "api places.googleapis.com method" in message
    }

    private fun Place.toNearbyPlace(userLat: Double, userLng: Double): NearbyPlace {
        val loc = location
        val lat = loc?.latitude ?: userLat
        val lng = loc?.longitude ?: userLng
        val placeId = id?.toString().orEmpty().ifBlank { "$lat-$lng" }
        val title = displayName?.toString()?.takeIf { it.isNotBlank() } ?: "Place"
        val typeLabel = primaryTypeDisplayName?.toString()
            ?.takeIf { it.isNotBlank() }
            ?: primaryType?.toString()?.takeIf { it.isNotBlank() }
            ?: types?.firstOrNull()?.toString().orEmpty()
        val photoMeta = resolvePhotoMetadata()
        if (photoMeta != null) {
            photoMetadataByPlaceId[placeId] = photoMeta
        }
        return NearbyPlace(
            id = placeId,
            name = title,
            address = formattedAddress?.toString().orEmpty(),
            latitude = lat,
            longitude = lng,
            rating = rating?.toDouble(),
            primaryType = typeLabel,
            distanceKm = distanceKm(userLat, userLng, lat, lng),
            openNow = resolveOpenNow(),
            userRatingsTotal = resolveUserRatingsTotal(),
            phoneNumber = resolvePhoneNumber(),
            hasPhoto = photoMeta != null,
        )
    }

    private fun Place.resolvePhotoMetadata(): PhotoMetadata? = runCatching {
        val metas = javaClass.methods.firstOrNull { it.name == "getPhotoMetadatas" }
            ?.invoke(this) as? List<*>
        metas?.filterIsInstance<PhotoMetadata>()?.firstOrNull()
    }.getOrNull()

    private fun Place.resolveOpenNow(): Boolean? = runCatching {
        val method = javaClass.methods.firstOrNull {
            it.name == "isOpen" && it.parameterCount == 0
        } ?: javaClass.methods.firstOrNull {
            it.name == "getOpenNow" && it.parameterCount == 0
        }
        when (val value = method?.invoke(this)) {
            is Boolean -> value
            else -> {
                val hours = runCatching {
                    javaClass.methods.firstOrNull { it.name == "getCurrentOpeningHours" }
                        ?.invoke(this)
                }.getOrNull()
                hours?.javaClass?.methods?.firstOrNull {
                    it.name == "getOpenNow" || it.name == "isOpenNow"
                }?.invoke(hours) as? Boolean
            }
        }
    }.getOrNull()

    private fun Place.resolveUserRatingsTotal(): Int? = runCatching {
        val method = javaClass.methods.firstOrNull {
            it.name == "getUserRatingsTotal" || it.name == "getUserRatingCount"
        }
        when (val value = method?.invoke(this)) {
            is Int -> value
            is Number -> value.toInt()
            else -> null
        }
    }.getOrNull()

    private fun Place.resolvePhoneNumber(): String? = runCatching {
        val national = javaClass.methods.firstOrNull { it.name == "getNationalPhoneNumber" }
            ?.invoke(this) as? String
        val international = javaClass.methods.firstOrNull { it.name == "getInternationalPhoneNumber" }
            ?.invoke(this) as? String
        national?.takeIf { it.isNotBlank() } ?: international?.takeIf { it.isNotBlank() }
    }.getOrNull()

    private suspend fun <T> awaitTask(block: () -> com.google.android.gms.tasks.Task<T>): T =
        suspendCancellableCoroutine { cont ->
            block()
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resumeWith(Result.failure(it)) }
        }
}
