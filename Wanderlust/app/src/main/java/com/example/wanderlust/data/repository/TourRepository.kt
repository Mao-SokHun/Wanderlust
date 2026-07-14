package com.example.wanderlust.data.repository

import com.example.wanderlust.data.local.DbProvider
import com.example.wanderlust.data.local.toDomain
import com.example.wanderlust.data.local.toEntity
import com.example.wanderlust.data.model.Tour

object TourRepositoryProvider {
    val instance: TourRepository by lazy { TourRepository() }
}

class TourRepository {

    private val dao = DbProvider.db().tourDao()

    suspend fun getCachedTours(): List<Tour> =
        dao.getAll().map { it.toDomain() }

    suspend fun getTours(search: String? = null, category: String? = null): Result<List<Tour>> {
        val cached = getCachedTours()
        return apiCall {
            it.getTours(
                search = search?.takeIf { s -> s.isNotBlank() },
                category = category?.takeIf { c -> c.isNotBlank() },
            )
        }.fold(
            onSuccess = { tours ->
                dao.clear()
                dao.upsertAll(tours.map { it.toEntity() })
                Result.success(tours)
            },
            onFailure = { error ->
                if (cached.isNotEmpty()) {
                    Result.success(filterTours(cached, search, category))
                } else {
                    Result.failure(error)
                }
            },
        )
    }

    private fun filterTours(tours: List<Tour>, search: String?, category: String?): List<Tour> {
        val q = search?.trim().orEmpty()
        return tours.filter { tour ->
            val matchesCat = category.isNullOrBlank() ||
                tour.category.equals(category, ignoreCase = true)
            val matchesQuery = q.isBlank() ||
                tour.title.contains(q, ignoreCase = true) ||
                tour.description.contains(q, ignoreCase = true) ||
                tour.category.contains(q, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }
}
