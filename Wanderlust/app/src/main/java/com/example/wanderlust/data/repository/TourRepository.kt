package com.example.wanderlust.data.repository

import com.example.wanderlust.data.local.DbProvider
import com.example.wanderlust.data.local.toDomain
import com.example.wanderlust.data.local.toEntity
import com.example.wanderlust.data.model.MyTourRatingResponse
import com.example.wanderlust.data.model.RateTourRequest
import com.example.wanderlust.data.model.RateTourResponse
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.SessionManager

object TourRepositoryProvider {
    val instance: TourRepository by lazy { TourRepository() }
}

data class TourQuery(
    val search: String? = null,
    val category: String? = null,
    val listingType: String? = null,
    val minRating: Double? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sort: String? = null,
    val limit: Int? = null,
    val top: Int? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val radiusKm: Double? = null,
)

class TourRepository {

    private val dao = DbProvider.db().tourDao()

    suspend fun getCachedTours(): List<Tour> =
        dao.getAll().map { it.toDomain() }

    suspend fun clearCache() {
        dao.clear()
    }

    suspend fun getTours(
        search: String? = null,
        category: String? = null,
        listingType: String? = null,
        minRating: Double? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sort: String? = null,
        limit: Int? = null,
        top: Int? = null,
        lat: Double? = null,
        lng: Double? = null,
        radiusKm: Double? = null,
    ): Result<List<Tour>> {
        val query = TourQuery(
            search = search,
            category = category,
            listingType = listingType,
            minRating = minRating,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            limit = limit,
            top = top,
            lat = lat,
            lng = lng,
            radiusKm = radiusKm,
        )
        val cached = getCachedTours()
        return apiCall {
            it.getTours(
                search = query.search?.takeIf { s -> s.isNotBlank() },
                category = query.category?.takeIf { c -> c.isNotBlank() },
                listingType = query.listingType?.takeIf { t -> t.isNotBlank() },
                minRating = query.minRating,
                minPrice = query.minPrice,
                maxPrice = query.maxPrice,
                sort = query.sort,
                limit = query.limit,
                top = query.top,
                lat = query.lat,
                lng = query.lng,
                radiusKm = query.radiusKm,
            )
        }.fold(
            onSuccess = { tours ->
                if (query.isCacheable()) {
                    dao.clear()
                    dao.upsertAll(tours.map { it.toEntity() })
                }
                Result.success(tours)
            },
            onFailure = { error ->
                if (cached.isNotEmpty() && query.isCacheable()) {
                    Result.success(filterTours(cached, query))
                } else {
                    Result.failure(error)
                }
            },
        )
    }

    suspend fun rateTour(tourId: String, stars: Int, comment: String = ""): Result<RateTourResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in to rate"))
        return apiCall { it.rateTour(header, tourId, RateTourRequest(stars, comment)) }
    }

    suspend fun getMyRating(tourId: String): Result<MyTourRatingResponse> {
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return apiCall { it.getMyTourRating(header, tourId) }
    }

    private fun TourQuery.isCacheable(): Boolean =
        listingType.isNullOrBlank() &&
            minRating == null &&
            minPrice == null &&
            maxPrice == null &&
            top == null &&
            lat == null &&
            sort.isNullOrBlank()

    private fun filterTours(tours: List<Tour>, query: TourQuery): List<Tour> {
        val q = query.search?.trim().orEmpty()
        return tours.filter { tour ->
            val matchesCat = query.category.isNullOrBlank() ||
                tour.category.equals(query.category, ignoreCase = true)
            val matchesType = query.listingType.isNullOrBlank() ||
                tour.listingType.equals(query.listingType, ignoreCase = true)
            val matchesRating = query.minRating == null || tour.rating >= query.minRating
            val matchesMinPrice = query.minPrice == null ||
                (tour.priceUsd != null && tour.priceUsd >= query.minPrice)
            val matchesMaxPrice = query.maxPrice == null ||
                (tour.priceUsd != null && tour.priceUsd <= query.maxPrice)
            val matchesQuery = q.isBlank() ||
                tour.title.contains(q, ignoreCase = true) ||
                tour.description.contains(q, ignoreCase = true) ||
                tour.category.contains(q, ignoreCase = true)
            matchesCat && matchesType && matchesRating && matchesMinPrice && matchesMaxPrice && matchesQuery
        }.let { list ->
            when (query.sort) {
                "price_asc" -> list.sortedBy { it.priceUsd ?: Double.MAX_VALUE }
                "price_desc" -> list.sortedByDescending { it.priceUsd ?: -1.0 }
                "rating" -> list.sortedByDescending { it.rating }
                else -> list.sortedByDescending { it.id.toIntOrNull() ?: 0 } // newest
            }
        }
    }
}
