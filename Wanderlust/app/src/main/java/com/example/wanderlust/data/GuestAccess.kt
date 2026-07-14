package com.example.wanderlust.data

import com.example.wanderlust.data.DestinationCatalog.allDestinations

/** Guest users see a small preview; full catalog requires sign-in. */
object GuestAccess {
    const val PREVIEW_LIMIT = 4

    private val previewIds: Set<String> by lazy {
        allDestinations.take(PREVIEW_LIMIT).map { it.id }.toSet()
    }

    fun isLoggedIn(): Boolean = SessionManager.isLoggedIn()

    fun <T> limitForGuest(items: List<T>): List<T> =
        if (isLoggedIn()) items else items.take(PREVIEW_LIMIT)

    fun canViewDestination(destination: DestinationCard): Boolean =
        when {
            destination.isCustomPlace || destination.id.startsWith("custom-") -> isLoggedIn()
            isLoggedIn() -> true
            else -> destination.id in previewIds
        }

    fun totalPlaceCount(): Int = allDestinations.size
}
