package com.example.wanderlust.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Lightweight recent nearby places for Home (not synced to server). */
data class RecentNearbyPlace(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double? = null,
    val primaryType: String = "",
    val phoneNumber: String? = null,
    val hasPhoto: Boolean = false,
)

object RecentPlacesStore {
    private const val PREFS = "wanderlust_recent_places"
    private const val KEY_JSON = "recent_json"
    private const val MAX = 8

    fun load(context: Context): List<RecentNearbyPlace> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_JSON, null)
            ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        RecentNearbyPlace(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            address = o.optString("address", ""),
                            latitude = o.getDouble("latitude"),
                            longitude = o.getDouble("longitude"),
                            rating = o.optDouble("rating").takeIf { o.has("rating") && !o.isNull("rating") },
                            primaryType = o.optString("primaryType", ""),
                            phoneNumber = o.optString("phone").takeIf { it.isNotBlank() },
                            hasPhoto = o.optBoolean("hasPhoto", false),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun remember(context: Context, place: RecentNearbyPlace) {
        val current = load(context).filterNot { it.id == place.id }
        val next = (listOf(place) + current).take(MAX)
        val arr = JSONArray()
        next.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("address", p.address)
                    put("latitude", p.latitude)
                    put("longitude", p.longitude)
                    if (p.rating != null) put("rating", p.rating) else put("rating", JSONObject.NULL)
                    put("primaryType", p.primaryType)
                    put("phone", p.phoneNumber.orEmpty())
                    put("hasPhoto", p.hasPhoto)
                },
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_JSON, arr.toString())
            .apply()
    }
}
