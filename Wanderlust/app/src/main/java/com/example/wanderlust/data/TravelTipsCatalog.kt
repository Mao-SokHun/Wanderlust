package com.example.wanderlust.data

/**
 * Practical Cambodia travel tips — advice only, not places/tours to book.
 * Distinct from Home (nearby Google Places) and Help Center (app support).
 */
data class TravelTip(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
)

object TravelTipsCatalog {
    val categories: List<String> = listOf(
        "Transport",
        "Money",
        "Safety",
        "Culture",
        "Connectivity",
    )

    val all: List<TravelTip> = listOf(
        TravelTip(
            id = "tuk-tuk",
            category = "Transport",
            title = "Agree the tuk-tuk fare first",
            body = "Before you sit down, confirm the price or use a metered/app ride. Short city hops are cheap; temple days and airport runs cost more.",
        ),
        TravelTip(
            id = "passapp",
            category = "Transport",
            title = "Try PassApp or local ride apps",
            body = "In Phnom Penh and Siem Reap, ride apps often show clearer fares than street haggling — useful if you are new to the city.",
        ),
        TravelTip(
            id = "buses",
            category = "Transport",
            title = "Night buses between cities",
            body = "Long routes (e.g. Phnom Penh ↔ Siem Reap / Sihanoukville) often use sleeper buses. Book early in peak season and keep valuables with you.",
        ),
        TravelTip(
            id = "usd-khr",
            category = "Money",
            title = "USD and riel both work",
            body = "US dollars are widely accepted. Small change often comes back in riel. Keep low-value notes for markets and tuk-tuks.",
        ),
        TravelTip(
            id = "atms",
            category = "Money",
            title = "Use bank ATMs when you can",
            body = "Prefer ATMs at major banks. Tell your bank you are traveling, and avoid letting cards out of sight when paying.",
        ),
        TravelTip(
            id = "tipping",
            category = "Money",
            title = "Tipping is appreciated, not always required",
            body = "Rounding up or leaving a small tip for good service is common in restaurants and for guides, but it is not as rigid as in some countries.",
        ),
        TravelTip(
            id = "bags",
            category = "Safety",
            title = "Keep bags closed in crowds",
            body = "Use a zippered bag in markets and on busy streets. Don’t flash phones or cash near traffic when negotiating rides.",
        ),
        TravelTip(
            id = "water",
            category = "Safety",
            title = "Drink sealed bottled water",
            body = "Tap water is not for drinking. Ice in busy restaurants is usually fine; choose sealed bottles on long trips.",
        ),
        TravelTip(
            id = "sun",
            category = "Safety",
            title = "Prepare for heat and sun",
            body = "Temple days mean lots of walking. Bring water, sunscreen, a hat, and go early or late to avoid midday heat.",
        ),
        TravelTip(
            id = "temple-dress",
            category = "Culture",
            title = "Cover shoulders and knees at temples",
            body = "Dress modestly for pagodas and palace grounds. Carry a light scarf or long pants if you wear shorts elsewhere.",
        ),
        TravelTip(
            id = "remove-shoes",
            category = "Culture",
            title = "Shoes off indoors",
            body = "Remove shoes when entering homes, some shops, and temple buildings. Watch for racks or signs at the entrance.",
        ),
        TravelTip(
            id = "greetings",
            category = "Culture",
            title = "A smile and “Sous-dei” go far",
            body = "A friendly greeting and patience help more than perfect Khmer. Learn a few thank-you words — locals notice the effort.",
        ),
        TravelTip(
            id = "sim",
            category = "Connectivity",
            title = "Local SIM is cheap and easy",
            body = "Cellcard, Smart, and Metfone sell tourist SIMs at airports and city shops. Bring your passport if asked for registration.",
        ),
        TravelTip(
            id = "offline-maps",
            category = "Connectivity",
            title = "Download maps offline",
            body = "Save your city area in Google Maps before you go. Helps when data is slow or you are in temples without signal.",
        ),
        TravelTip(
            id = "power",
            category = "Connectivity",
            title = "Bring a dual USB charger",
            body = "Outlets are mostly Type A/C/G mix. A compact multi-port charger and a power bank cover long temple or bus days.",
        ),
    )

    fun filter(category: String? = null, query: String = ""): List<TravelTip> {
        val q = query.trim()
        return all.filter { tip ->
            val catOk = category == null || tip.category.equals(category, ignoreCase = true)
            val queryOk = q.isBlank() ||
                tip.title.contains(q, ignoreCase = true) ||
                tip.body.contains(q, ignoreCase = true) ||
                tip.category.contains(q, ignoreCase = true)
            catOk && queryOk
        }
    }
}
