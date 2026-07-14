package com.example.wanderlust.data

import com.example.wanderlust.data.local.FavoriteEntity
import com.example.wanderlust.data.model.Tour

fun Tour.toDestinationCard(): DestinationCard {
    val catalog = DestinationCatalog.findByTitle(title)
    if (catalog != null) {
        return catalog.copy(
            id = id,
            description = description.ifBlank { catalog.description },
            rating = rating,
            ratingCount = ratingCount,
            listingType = listingType,
            priceUsd = priceUsd,
            distanceKm = distanceKm,
            businessName = businessName,
        )
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
            ratingCount = ratingCount,
            listingType = listingType,
        )
    }
    val loc = location.ifBlank {
        serviceArea.ifBlank {
            businessName?.takeIf { it.isNotBlank() }?.let { "$it • Cambodia" }
                ?: "Cambodia • $category"
        }
    }
    val price = priceLabel.ifBlank {
        when {
            priceUsd != null && listingType == "VEHICLE" ->
                "$${priceUsd.toInt()} / ${rateUnit.ifBlank { "day" }}"
            priceUsd != null -> "$${priceUsd.toInt()}"
            businessName != null -> businessName
            else -> if (listingType == "VEHICLE") "Transport" else "Tour"
        }
    }
    return DestinationCard(
        id = id,
        title = title,
        location = loc,
        locationKh = loc,
        rating = rating,
        ratingCount = ratingCount,
        priceLabel = price,
        duration = duration.ifBlank {
            listOfNotNull(
                vehicleType.takeIf { it.isNotBlank() },
                seats?.let { "$it seats" },
            ).joinToString(" · ")
        },
        imageUrl = imageUrl.ifBlank { WanderlustImages.imageForTour(title, category, id) },
        category = category,
        categoryKh = CambodiaLabels.categoryKh(category),
        description = description,
        latitude = latitude,
        longitude = longitude,
        listingType = listingType,
        vehicleType = vehicleType,
        seats = seats,
        rateUnit = rateUnit,
        priceUsd = priceUsd,
        distanceKm = distanceKm,
        businessName = businessName,
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
