package com.example.wanderlust.data

object CambodiaLabels {
    val categoryKh = mapOf(
        "Beach" to "ឆ្នេរ",
        "Mountain" to "ភ្នំ",
        "Temple" to "ប្រាសាទ",
        "City" to "ទីក្រុង",
        "Food" to "អាហារ",
    )

    fun categoryKh(category: String): String = categoryKh[category] ?: category

    data class BilingualCategory(val en: String, val kh: String)

    val categories: List<BilingualCategory> = listOf(
        BilingualCategory("Beach", "ឆ្នេរ"),
        BilingualCategory("Mountain", "ភ្នំ"),
        BilingualCategory("Temple", "ប្រាសាទ"),
        BilingualCategory("City", "ទីក្រុង"),
        BilingualCategory("Food", "អាហារ"),
    )
}
