package com.example.wanderlust.data

/** Cambodia tours — English + Khmer + real photo per place */
data class DestinationCard(
    val id: String,
    val title: String,
    val location: String,
    val rating: Double,
    val priceLabel: String,
    val duration: String = "",
    val imageUrl: String,
    val category: String,
    val description: String = "",
    val titleKh: String = "",
    val locationKh: String = "",
    val descriptionKh: String = "",
    val categoryKh: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isCustomPlace: Boolean = false,
)

object DestinationCatalog {
    val categories = CambodiaLabels.categories.map { it.en }

    val allDestinations: List<DestinationCard> = listOf(
        cambodia("angkor-wat", "Angkor Wat Sunrise", "អង្គរវត្ត ថ្ងៃរះ",
            "Siem Reap, Cambodia", "សៀមរាប", "Temple", 4.9, "From \$35", "1 Day",
            "Watch sunrise over the world-famous Khmer temple complex.",
            "ទស្សនាទីរសរព្វថ្ងៃរះ និងរុករកប្រាសាទខ្មែរដ៏ល្បីល្បាញ"),
        cambodia("bayon", "Bayon & Angkor Thom", "បាយ័ន និងអង្គរធំ",
            "Siem Reap, Cambodia", "សៀមរាប", "Temple", 4.8, "From \$28", "Half Day",
            "Smiling stone faces, terraces, and jungle-covered ruins.",
            "មុខពោធិសត្វ លេខដាច់ស្តី និងប្រាសាទក្នុងព្រៃ"),
        cambodia("koh-rong", "Koh Rong Island", "កោះរ៉ុង",
            "Preah Sihanouk, Cambodia", "ព្រះសីហនុ", "Beach", 4.8, "From \$45", "2–3 Days",
            "White sand, turquoise water, snorkeling, and island bungalows.",
            "ខ្សាច់ស ទឹកខៀវ ង្រួតមុខទឹក និងបរិវេណកោះ"),
        cambodia("koh-tonsay", "Koh Tonsay (Rabbit Island)", "កោះទន្សាយ",
            "Kep, Cambodia", "កែប", "Beach", 4.6, "From \$25", "1 Day",
            "Quiet beaches near Kep with fresh seafood.",
            "ឆ្នេរស្ងៀម កែប អាហារសមុទ្រស្រស់"),
        cambodia("bokor", "Bokor Mountain & Old Casino", "ភ្នំបូកគោ",
            "Kampot, Cambodia", "កំពត", "Mountain", 4.7, "From \$30", "1 Day",
            "Cool mist, colonial ruins, waterfalls, and coastal views.",
            "អាកាសត្រជាក់ អាគាររកប្រាសាទ និងទឹកជ្រុល"),
        cambodia("phnom-kulen", "Phnom Kulen National Park", "ភ្នំគូលេន",
            "Siem Reap, Cambodia", "សៀមរាប", "Mountain", 4.7, "From \$40", "1 Day",
            "Sacred mountain, riverbed carvings, and jungle waterfalls.",
            "ភ្នំពិសិដ្ឋ រូបចម្លាក់លើថ្ម និងទឹកជ្រុល"),
        cambodia("royal-palace", "Royal Palace & Silver Pagoda", "ព្រះបរមរាជវាំង",
            "Phnom Penh, Cambodia", "ភ្នំពេញ", "City", 4.6, "From \$10", "Half Day",
            "Golden spires, Khmer architecture, and riverside gardens.",
            "ទីប្រកាសមាស ស្ថាបត្យកម្ពុជា សួនជាប់មាត់ទន្លេ"),
        cambodia("central-market-food", "Phnom Penh Street Food Tour", "អាហារតាមផ្លូវភ្នំពេញ",
            "Phnom Penh, Cambodia", "ភ្នំពេញ", "Food", 4.5, "From \$18", "3 Hours",
            "Nom banh chok, grilled seafood, and night markets.",
            "នំបញ្ចុក អាហារអាំង និងផ្សារយប់"),
        cambodia("battambang", "Battambang Bamboo Train", "រថភ្លើងឬស្សីបាត់ដំបង",
            "Battambang, Cambodia", "បាត់ដំបង", "City", 4.6, "From \$15", "1 Day",
            "Norry ride, colonial streets, and bat caves at dusk.",
            "រថភ្លើងឈរបណ្ដោះអាសន្ន ផ្លូវអាណានិគម ល្បារជូល"),
        cambodia("kampot-pepper", "Kampot Pepper Farm Tour", "សួនម្រេចកំពត",
            "Kampot, Cambodia", "កំពត", "Food", 4.7, "From \$20", "Half Day",
            "Famous Kampot pepper plantations and riverside lunch.",
            "សួនម្រេចកំពតល្បី និងអាហារតាមរតន្ត"),
        cambodia("koh-ker", "Koh Ker Temple Complex", "ប្រាសាទកោះកែរ",
            "Preah Vihear, Cambodia", "ព្រះវិហារ", "Temple", 4.5, "From \$55", "1 Day",
            "Remote pyramid temple Prasat Thom away from crowds.",
            "ប្រាសាទពירមីដ ឆ្ងាយពីភាពឯកោ"),
        cambodia("tonle-sap", "Tonle Sap Floating Village", "ភូមិអណ្ដែតទន្លេសាប",
            "Siem Reap, Cambodia", "សៀមរាប", "City", 4.4, "From \$22", "Half Day",
            "Boat tour through floating homes and fishing communities.",
            "ដំណើរកម្សោលភូមិអណ្ដែត និងសហគមន៍អ្នកនេសាទ"),
        cambodia("kirirom", "Kirirom National Park", "ឧទ្យានជាតិគីរីរម្យ",
            "Kampong Speu, Cambodia", "កំពង់ស្ពឺ", "Mountain", 4.5, "From \$35", "1 Day",
            "Pine forest hikes, waterfalls, and cool climate.",
            "ព្រៃស្រល់ ទឹកជ្រុល អាកាសត្រជាក់"),
        cambodia("oudong", "Oudong Mountain Temples", "ភ្នំឧដុង្គ",
            "Kandal, Cambodia", "កណ្ដាល", "Temple", 4.4, "From \$12", "Half Day",
            "Former capital stupas and countryside views.",
            "ចោតីអតីតរាជធានី ទេសភាពជនបទ"),
        cambodia("pub-street", "Siem Reap Pub Street & Night Market", "ផាបរឿត និងផ្សារយប់",
            "Siem Reap, Cambodia", "សៀមរាប", "Food", 4.8, "From \$15", "Evening",
            "Khmer BBQ, fruit shakes, handicrafts, and live music.",
            "បុកល្បង់ខ្មែរ ទឹកផ្លែឈើ សិប្បកម្ម និងតន្ត្រី"),
        cambodia("preah-vihear", "Preah Vihear Temple", "ប្រាសាទព្រះវិហារ",
            "Preah Vihear, Cambodia", "ព្រះវិហារ", "Temple", 4.7, "From \$50", "1 Day",
            "Clifftop UNESCO temple on the Dangrek range.",
            "ប្រាសាទលើជម្រំភ្នំដងរែក"),
        cambodia("kratie-dolphins", "Mekong Irrawaddy Dolphins", "ផ្សោតអៀរវ៉ាដី មេគង្គ",
            "Kratie, Cambodia", "ក្រចេះ", "City", 4.5, "From \$25", "1 Day",
            "Boat trip to spot rare freshwater dolphins.",
            "ដំណើរកម្សោលមើលផ្សោតទឹកផ្អាក"),
        cambodia("mondulkiri", "Mondulkiri Jungle Trek", "ដំណើរព្រៃមណ្ឌលគិរី",
            "Mondulkiri, Cambodia", "មណ្ឌលគិរី", "Mountain", 4.6, "From \$60", "2 Days",
            "Hill tribe villages, waterfalls, and jungle trails.",
            "ភូមិជនជាតិភាគតិច ទឹកជ្រុល ផ្លូវព្រៃ"),
    )

    val popularDestinations: List<DestinationCard> = allDestinations
    val savedPlaces: List<DestinationCard> = emptyList()

    fun findById(id: String): DestinationCard? = allDestinations.firstOrNull { it.id == id }

    fun findByTitle(title: String): DestinationCard? =
        allDestinations.firstOrNull {
            it.title.equals(title, ignoreCase = true) || it.titleKh == title
        }

    fun filter(category: String? = null, query: String = ""): List<DestinationCard> =
        allDestinations.filter { dest ->
            val matchesCat = category == null || dest.category.equals(category, ignoreCase = true)
            val q = query.trim()
            val matchesQuery = q.isBlank() ||
                dest.title.contains(q, ignoreCase = true) ||
                dest.titleKh.contains(q, ignoreCase = false) ||
                dest.location.contains(q, ignoreCase = true) ||
                dest.locationKh.contains(q, ignoreCase = false) ||
                dest.description.contains(q, ignoreCase = true) ||
                dest.descriptionKh.contains(q, ignoreCase = false) ||
                dest.category.contains(q, ignoreCase = true) ||
                dest.categoryKh.contains(q, ignoreCase = false)
            matchesCat && matchesQuery
        }

    private fun cambodia(
        id: String,
        title: String,
        titleKh: String,
        location: String,
        locationKh: String,
        category: String,
        rating: Double,
        priceLabel: String,
        duration: String,
        description: String,
        descriptionKh: String,
    ) = DestinationCard(
        id = id,
        title = title,
        titleKh = titleKh,
        location = location,
        locationKh = locationKh,
        rating = rating,
        priceLabel = priceLabel,
        duration = duration,
        imageUrl = WanderlustImages.forPlace(id),
        category = category,
        categoryKh = CambodiaLabels.categoryKh(category),
        description = description,
        descriptionKh = descriptionKh,
    )
}
