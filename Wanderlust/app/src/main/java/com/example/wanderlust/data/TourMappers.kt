package com.example.wanderlust.data

import com.example.wanderlust.data.local.FavoriteEntity
import com.example.wanderlust.data.model.Tour

fun Tour.toDestinationCard(): DestinationCard {
    val catalog = DestinationCatalog.findByTitle(title)
    if (catalog != null) {
        return catalog.copy(id = id, description = description.ifBlank { catalog.description })
    }
    if (id.startsWith("custom-")) {
        return DestinationCard(
            id = id,
            title = title,
            location = description.ifBlank { "Cambodia" },
            rating = rating,
            priceLabel = "My list",
            imageUrl = WanderlustImages.imageForTour(title, category, id),
            category = category,
            categoryKh = category,
            description = description,
            isCustomPlace = true,
        )
    }
    return DestinationCard(
        id = id,
        title = title,
        location = "Cambodia • $category",
        locationKh = "កម្ពុជា • ${CambodiaLabels.categoryKh(category)}",
        rating = rating,
        priceLabel = "Suggested",
        imageUrl = WanderlustImages.imageForTour(title, category, id),
        category = category,
        categoryKh = CambodiaLabels.categoryKh(category),
        description = description,
    )
}

fun FavoriteEntity.toDestinationCard(): DestinationCard {
    val catalog = DestinationCatalog.findByTitle(title)
    if (catalog != null && !isCustom) {
        return catalog.copy(id = tourId, description = description.ifBlank { catalog.description })
    }
    return DestinationCard(
        id = tourId,
        title = title,
        location = location.ifBlank { "Cambodia" },
        locationKh = location,
        rating = rating,
        priceLabel = if (isCustom) "My list" else "In your list",
        imageUrl = WanderlustImages.imageForTour(title, category, tourId),
        category = category,
        categoryKh = if (isCustom) category else CambodiaLabels.categoryKh(category),
        description = description,
        isCustomPlace = isCustom,
        latitude = latitude,
        longitude = longitude,
    )
}

fun Tour.toFavoriteEntity(userId: String): FavoriteEntity = FavoriteEntity(
    userId = userId,
    tourId = id,
    title = title,
    description = description,
    category = category,
    rating = rating,
)
