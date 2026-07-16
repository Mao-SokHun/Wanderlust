package com.example.wanderlust.data

/**
 * Public contact for the Wanderlust owner / developer.
 * Used in About, Help Center, and Settings.
 */
object AdminContact {
    const val OWNER_NAME = "Mao SokHun"
    const val OWNER_ROLE = "Developer & Owner"
    const val OWNER_ROLE_KH = "អ្នកអភិវឌ្ឍន៍ និងម្ចាស់កម្មវិធី"

    const val TELEGRAM_USERNAME = "sokhunmao"
    const val TELEGRAM_URL = "https://t.me/$TELEGRAM_USERNAME"

    const val EMAIL = "sokhunmao390@gmail.com"

    /** Primary phone (Cellcard / call / WhatsApp). */
    const val PHONE = "0974944390"

    /** Alternate phone. */
    const val PHONE_ALT = "0885459115"

    val PHONES: List<String> = listOf(PHONE, PHONE_ALT)

    const val PROJECT_NOTE = "Wanderlust · Cambodia travel marketplace"
    const val PROJECT_NOTE_KH = "Wanderlust · ទីផ្សារទេសចរណ៍កម្ពុជា"
}
