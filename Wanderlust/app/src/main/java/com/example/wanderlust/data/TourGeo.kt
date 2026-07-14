package com.example.wanderlust.data

fun weatherLabel(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2, 3 -> "Partly cloudy"
    45, 48 -> "Fog"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rain"
    71, 73, 75 -> "Rain"
    80, 81, 82 -> "Rain showers"
    95 -> "Thunderstorm"
    else -> "Mixed conditions"
}

data class GeoLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

fun geoForDestination(title: String, category: String): GeoLocation = when {
    title.contains("Angkor Wat", ignoreCase = true) ->
        GeoLocation("Angkor Wat, Siem Reap", 13.4125, 103.8667)
    title.contains("Bayon", ignoreCase = true) || title.contains("Angkor Thom", ignoreCase = true) ->
        GeoLocation("Angkor Thom, Siem Reap", 13.4411, 103.8591)
    title.contains("Koh Rong", ignoreCase = true) ->
        GeoLocation("Koh Rong, Sihanoukville", 10.7230, 103.2540)
    title.contains("Rabbit", ignoreCase = true) || title.contains("Koh Tonsay", ignoreCase = true) ->
        GeoLocation("Kep, Cambodia", 10.4833, 104.3167)
    title.contains("Bokor", ignoreCase = true) ->
        GeoLocation("Bokor Mountain, Kampot", 10.6380, 104.0220)
    title.contains("Kulen", ignoreCase = true) ->
        GeoLocation("Phnom Kulen, Siem Reap", 13.6090, 104.1140)
    title.contains("Royal Palace", ignoreCase = true) || title.contains("Phnom Penh", ignoreCase = true) ->
        GeoLocation("Phnom Penh, Cambodia", 11.5564, 104.9282)
    title.contains("Battambang", ignoreCase = true) ->
        GeoLocation("Battambang, Cambodia", 13.0957, 103.2022)
    title.contains("Kampot", ignoreCase = true) ->
        GeoLocation("Kampot, Cambodia", 10.6104, 104.1815)
    title.contains("Koh Ker", ignoreCase = true) ->
        GeoLocation("Koh Ker, Preah Vihear", 13.7850, 104.5380)
    title.contains("Preah Vihear", ignoreCase = true) ->
        GeoLocation("Preah Vihear Temple", 14.3920, 104.6840)
    title.contains("Tonle Sap", ignoreCase = true) ->
        GeoLocation("Tonle Sap, Siem Reap", 13.3500, 103.9000)
    title.contains("Kirirom", ignoreCase = true) ->
        GeoLocation("Kirirom, Kampong Speu", 11.3000, 104.0500)
    title.contains("Oudong", ignoreCase = true) ->
        GeoLocation("Oudong, Kandal", 11.6000, 104.7000)
    title.contains("Pub Street", ignoreCase = true) || title.contains("Siem Reap", ignoreCase = true) ->
        GeoLocation("Siem Reap, Cambodia", 13.3633, 103.8564)
    title.contains("Kratie", ignoreCase = true) || title.contains("Dolphin", ignoreCase = true) ->
        GeoLocation("Kratie, Cambodia", 12.4880, 106.0180)
    title.contains("Mondulkiri", ignoreCase = true) ->
        GeoLocation("Sen Monorom, Mondulkiri", 12.4500, 107.2000)
    category.equals("Beach", ignoreCase = true) ->
        GeoLocation("Sihanoukville, Cambodia", 10.6340, 103.5230)
    category.equals("Mountain", ignoreCase = true) ->
        GeoLocation("Kampot, Cambodia", 10.6104, 104.1815)
    category.equals("Temple", ignoreCase = true) ->
        GeoLocation("Siem Reap, Cambodia", 13.3633, 103.8564)
    else -> GeoLocation("Phnom Penh, Cambodia", 11.5564, 104.9282)
}
