package com.example.wanderlust.data

import com.example.wanderlust.data.local.FavoriteEntity
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.data.model.durationLabel
import com.example.wanderlust.data.model.routeLabel
import com.example.wanderlust.data.model.scheduleLabel

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
            packageDetails = packageDetails,
            tripDetails = tripDetails,
            rentalDetails = rentalDetails,
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
            packageDetails = packageDetails,
            tripDetails = tripDetails,
            rentalDetails = rentalDetails,
        )
    }
    val loc = when {
        listingType == "TRIP" && tripDetails != null ->
            tripDetails.routeLabel().ifBlank { location }
        else -> location.ifBlank {
            serviceArea.ifBlank {
                businessName?.takeIf { it.isNotBlank() }?.let { "$it • Cambodia" }
                    ?: "Cambodia • $category"
            }
        }
    }
    val price = priceLabel.ifBlank {
        when {
            priceUsd != null && listingType == "TRIP" -> "$${priceUsd.toInt()} / seat"
            priceUsd != null && (listingType == "RENTAL" || listingType == "VEHICLE") ->
                "$${priceUsd.toInt()} / ${rateUnit.ifBlank { "day" }}"
            priceUsd != null -> {
                val unit = when (packageDetails?.priceType) {
                    "group" -> " / group"
                    else -> " / person"
                }
                "$${priceUsd.toInt()}$unit"
            }
            businessName != null -> businessName
            else -> when (listingType) {
                "TRIP" -> "Bus trip"
                "RENTAL", "VEHICLE" -> "Car rental"
                else -> "Tour"
            }
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
            tripDetails?.scheduleLabel().orEmpty().ifBlank {
                packageDetails?.durationLabel().orEmpty().ifBlank {
                    listOfNotNull(
                        vehicleType.takeIf { it.isNotBlank() },
                        seats?.let { "$it seats" },
                        rentalDetails?.makeModel?.takeIf { it.isNotBlank() },
                    ).joinToString(" · ")
                }
            }
        },
        imageUrl = imageUrl.ifBlank {
            rentalDetails?.imageUrls?.firstOrNull()
                ?: packageDetails?.imageUrls?.firstOrNull()
                ?: tripDetails?.imageUrls?.firstOrNull().orEmpty()
                    .ifBlank { WanderlustImages.imageForTour(title, category, id) }
        },
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
        packageDetails = packageDetails,
        tripDetails = tripDetails,
        rentalDetails = rentalDetails,
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
