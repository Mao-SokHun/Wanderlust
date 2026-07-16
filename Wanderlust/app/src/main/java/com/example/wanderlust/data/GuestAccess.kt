package com.example.wanderlust.data

/**
 * Access rules:
 * - Guest + logged-in: browse / search places, open tours.
 * - Logged-in only: Saved list and save actions.
 */
object GuestAccess {
    fun isLoggedIn(): Boolean = SessionManager.isLoggedIn()

    /** Full lists for everyone — no preview cap. */
    fun <T> limitForGuest(items: List<T>): List<T> = items

    fun canViewDestination(destination: DestinationCard): Boolean = true

    fun canBrowseTours(): Boolean = true

    fun canUseSavedFeature(): Boolean = isLoggedIn()

    fun requiresAccountToSave(): Boolean = !canUseSavedFeature()

    fun totalPlaceCount(): Int = DestinationCatalog.allDestinations.size
}
