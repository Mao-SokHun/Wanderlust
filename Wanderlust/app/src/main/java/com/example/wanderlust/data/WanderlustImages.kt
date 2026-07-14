package com.example.wanderlust.data

/** Real Cambodia place photos (Unsplash) — one URL per destination id */
object WanderlustImages {
    private const val Q = "?w=960&q=85&fit=crop"

    private val placePhotos = mapOf(
        "angkor-wat" to "https://images.unsplash.com/photo-1539650116574-75c0c7d0f034$Q",
        "bayon" to "https://images.unsplash.com/photo-1589394815804-978e03338127$Q",
        "koh-rong" to "https://images.unsplash.com/photo-1559592413-e7f23d3d8f28$Q",
        "koh-tonsay" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3b$Q",
        "bokor" to "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b$Q",
        "phnom-kulen" to "https://images.unsplash.com/photo-1438786657495-640937046d18$Q",
        "royal-palace" to "https://images.unsplash.com/photo-1573843981265-be1999ff1372$Q",
        "central-market-food" to "https://images.unsplash.com/photo-1555939594-58d7cb561ad1$Q",
        "battambang" to "https://images.unsplash.com/photo-1548013146-724f043bb1c4$Q",
        "kampot-pepper" to "https://images.unsplash.com/photo-1518977676601-b53f82aba655$Q",
        "koh-ker" to "https://images.unsplash.com/photo-1565008576549-5756a63183c7$Q",
        "tonle-sap" to "https://images.unsplash.com/photo-1544551763-46a013bb70d5$Q",
        "kirirom" to "https://images.unsplash.com/photo-1441974231531-c6227db76b6e$Q",
        "oudong" to "https://images.unsplash.com/photo-1596422846544-75c6fcbd58b6$Q",
        "pub-street" to "https://images.unsplash.com/photo-1514933651103-005eec06e04b$Q",
        "preah-vihear" to "https://images.unsplash.com/photo-1565008576549-5756a63183c7$Q",
        "kratie-dolphins" to "https://images.unsplash.com/photo-1544551763-77ef2d0cfcbe$Q",
        "mondulkiri" to "https://images.unsplash.com/photo-1516426122078-c23e76319801$Q",
    )

    const val ANGKOR = "https://images.unsplash.com/photo-1539650116574-75c0c7d0f034$Q"
    const val TEMPLE = "https://images.unsplash.com/photo-1596422846544-75c6fcbd58b6$Q"
    const val BEACH = "https://images.unsplash.com/photo-1559592413-e7f23d3d8f28$Q"
    const val MOUNTAIN = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b$Q"
    const val CITY = "https://images.unsplash.com/photo-1573843981265-be1999ff1372$Q"
    const val FOOD = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1$Q"

    const val HERO_MOUNTAINS = MOUNTAIN
    const val BALI_RETREAT = BEACH
    const val SWISS_ALPS = MOUNTAIN
    const val BOOKING_HERO = CITY
    const val ADMIN_AVATAR =
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&q=80"
    const val USER_FELIX = ADMIN_AVATAR
    const val USER_MAYA = ADMIN_AVATAR
    const val USER_LIAM = ADMIN_AVATAR

    fun forPlace(id: String): String = placePhotos[id] ?: ANGKOR

    fun imageForTour(title: String, category: String, id: String? = null): String {
        if (id != null) {
            placePhotos[id]?.let { return it }
        }
        return when {
            title.contains("Angkor", ignoreCase = true) -> ANGKOR
            title.contains("Bayon", ignoreCase = true) -> placePhotos["bayon"] ?: TEMPLE
            title.contains("Koh Rong", ignoreCase = true) -> placePhotos["koh-rong"] ?: BEACH
            title.contains("Palace", ignoreCase = true) -> CITY
            title.contains("Pepper", ignoreCase = true) || title.contains("Food", ignoreCase = true) -> FOOD
            title.contains("Bokor", ignoreCase = true) || title.contains("Kulen", ignoreCase = true) -> MOUNTAIN
            title.contains("Tonle", ignoreCase = true) -> placePhotos["tonle-sap"] ?: CITY
            title.contains("Dolphin", ignoreCase = true) -> placePhotos["kratie-dolphins"] ?: CITY
            category.equals("Beach", ignoreCase = true) -> BEACH
            category.equals("Mountain", ignoreCase = true) -> MOUNTAIN
            category.equals("Temple", ignoreCase = true) -> TEMPLE
            category.equals("Food", ignoreCase = true) -> FOOD
            category.equals("City", ignoreCase = true) -> CITY
            else -> ANGKOR
        }
    }

    fun galleryForPlace(id: String, title: String, category: String): List<String> {
        val primary = forPlace(id)
        val extra = listOf(
            placePhotos["angkor-wat"] ?: ANGKOR,
            placePhotos["koh-rong"] ?: BEACH,
            placePhotos["royal-palace"] ?: CITY,
            placePhotos["central-market-food"] ?: FOOD,
        )
        return (listOf(primary) + extra).distinct()
    }

    fun galleryForTour(title: String, category: String, id: String? = null): List<String> =
        if (id != null) galleryForPlace(id, title, category)
        else galleryForPlace("angkor-wat", title, category)
}
