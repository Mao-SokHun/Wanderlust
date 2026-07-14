package com.example.wanderlust.data.repository

import com.example.wanderlust.data.DestinationCatalog
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.local.DbProvider
import com.example.wanderlust.data.model.CustomPlaceInput
import com.example.wanderlust.data.model.FavoriteRequest
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.toDestinationCard
import com.example.wanderlust.data.toFavoriteEntity
import java.util.UUID

class FavoriteRepository {

    private val dao = DbProvider.db().favoriteDao()
    private val tourRepository = TourRepositoryProvider.instance

    private fun userKey(): String = SessionManager.userId ?: "guest"

    suspend fun loadSavedDestinations(): Result<List<com.example.wanderlust.data.DestinationCard>> {
        val localCards = dao.getForUser(userKey()).map { it.toDestinationCard() }
        val header = SessionManager.authHeader()
        if (header == null) {
            return Result.success(localCards)
        }
        return apiCall { it.getFavorites(header) }.fold(
            onSuccess = { tours ->
                val custom = dao.getCustomForUser(userKey())
                dao.clearForUser(userKey())
                dao.upsertAll(
                    tours.map { apiTour ->
                        apiTour.toLocalTour().toFavoriteEntity(userKey())
                    } + custom,
                )
                Result.success(dao.getForUser(userKey()).map { it.toDestinationCard() })
            },
            onFailure = { error ->
                if (localCards.isNotEmpty()) Result.success(localCards) else Result.failure(error)
            },
        )
    }

    suspend fun isFavorite(tourId: String, title: String? = null): Boolean {
        if (dao.isFavorite(userKey(), tourId) > 0) return true
        val t = title?.trim().orEmpty()
        return t.isNotEmpty() && dao.isFavoriteByTitle(userKey(), t) > 0
    }

    suspend fun toggleFavorite(tour: Tour): Result<Boolean> {
        if (SessionManager.authHeader() == null) {
            return Result.failure(Exception("Please sign in to save places"))
        }
        val localId = tour.id
        val currentlySaved = isFavorite(localId, tour.title)
        return if (currentlySaved) {
            val serverId = resolveServerTourId(tour)
            runCatching {
                apiCall {
                    it.removeFavorite(
                        SessionManager.authHeader()!!,
                        serverId ?: localId,
                        tour.title,
                    )
                }
            }
            Result.success(Unit).map {
                dao.remove(userKey(), localId)
                if (serverId != null && serverId != localId) {
                    dao.remove(userKey(), serverId)
                }
                false
            }
        } else {
            val serverId = resolveServerTourId(tour)
            val header = SessionManager.authHeader()!!
            runCatching {
                apiCall { api ->
                    api.addFavorite(
                        header,
                        FavoriteRequest(
                            tourId = serverId ?: localId,
                            title = tour.title,
                        ),
                    )
                }
            }
            dao.upsertAll(
                listOf(
                    tour.copy(id = localId).toFavoriteEntity(userKey()),
                ),
            )
            Result.success(true)
        }
    }

    suspend fun addCustomPlace(input: CustomPlaceInput): Result<com.example.wanderlust.data.DestinationCard> {
        if (SessionManager.authHeader() == null) {
            return Result.failure(Exception("Please sign in to save places"))
        }
        val title = input.title.trim()
        val location = input.location.trim()
        if (title.isBlank() || location.isBlank()) {
            return Result.failure(Exception("Place name and location are required"))
        }
        val (lat, lng) = parseMapsCoordinates(input.mapsLink, input.latitude, input.longitude)
        val preferred = input.preferredId?.trim().orEmpty()
        val id = when {
            preferred.startsWith("nearby-") || preferred.startsWith("custom-") -> preferred
            preferred.isNotBlank() -> "nearby-$preferred"
            else -> "custom-${UUID.randomUUID()}"
        }
        // Avoid duplicates if already saved under this place
        if (dao.isFavorite(userKey(), id) > 0 || dao.isFavoriteByTitle(userKey(), title) > 0) {
            dao.getForUser(userKey())
                .firstOrNull { it.tourId == id || it.title.equals(title, ignoreCase = true) }
                ?.let { return Result.success(it.toDestinationCard()) }
        }
        val entity = com.example.wanderlust.data.local.FavoriteEntity(
            userId = userKey(),
            tourId = id,
            title = title,
            description = input.notes.trim(),
            category = "My plan",
            rating = 0.0,
            location = location,
            latitude = lat,
            longitude = lng,
            isCustom = true,
        )
        dao.upsertAll(listOf(entity))
        return Result.success(entity.toDestinationCard())
    }

    suspend fun savedGooglePlaceIds(): Set<String> {
        return dao.getCustomForUser(userKey())
            .mapNotNull { entity ->
                when {
                    entity.tourId.startsWith("nearby-") -> entity.tourId.removePrefix("nearby-")
                    else -> null
                }
            }
            .toSet()
    }

    suspend fun savedTitles(): Set<String> {
        return dao.getForUser(userKey()).map { it.title.trim().lowercase() }.toSet()
    }

    private suspend fun resolveServerTourId(tour: Tour): String? {
        if (tour.id.isNotBlank() && tour.id.all { it.isDigit() }) return tour.id
        fun match(list: List<Tour>): String? =
            list.firstOrNull { it.title.equals(tour.title, ignoreCase = true) }?.id

        match(tourRepository.getCachedTours())?.let { return it }
        tourRepository.getTours().getOrNull()?.let { resolved ->
            match(resolved)?.let { return it }
        }
        return null
    }

    private fun Tour.toLocalTour(): Tour {
        val catalog = DestinationCatalog.findByTitle(title)
        return if (catalog != null) copy(id = catalog.id) else this
    }

    private fun parseMapsCoordinates(
        mapsLink: String,
        lat: Double?,
        lng: Double?,
    ): Pair<Double?, Double?> {
        if (lat != null && lng != null) return lat to lng
        val link = mapsLink.trim()
        if (link.isBlank()) return null to null
        val atMatch = Regex("""@(-?\d+\.?\d*),(-?\d+\.?\d*)""").find(link)
        if (atMatch != null) {
            return atMatch.groupValues[1].toDoubleOrNull() to atMatch.groupValues[2].toDoubleOrNull()
        }
        val qMatch = Regex("""[?&]q=(-?\d+\.?\d*),(-?\d+\.?\d*)""").find(link)
        if (qMatch != null) {
            return qMatch.groupValues[1].toDoubleOrNull() to qMatch.groupValues[2].toDoubleOrNull()
        }
        return null to null
    }
}
