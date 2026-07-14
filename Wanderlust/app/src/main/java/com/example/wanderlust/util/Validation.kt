package com.example.wanderlust.util

import com.example.wanderlust.locale.AppLocale

/**
 * Shared client-side validation — keep rules aligned with `backend/utils/validate.js`.
 */
object Validation {
    const val NAME_MIN = 2
    const val NAME_MAX = 60
    const val EMAIL_MAX = 120
    const val PASSWORD_MIN = 6
    const val PASSWORD_MAX = 72
    const val TITLE_MAX = 120
    const val DESCRIPTION_MAX = 2000
    const val LOCATION_MAX = 200
    const val COMPANY_MAX = 150
    const val BIO_MAX = 280
    const val PHONE_MAX = 40
    const val COMMENT_MAX = 500
    const val PRICE_MAX = 100_000.0
    const val SEATS_MIN = 1
    const val SEATS_MAX = 60

    private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    private fun msg(en: String, kh: String): String =
        if (AppLocale.isKhmer) kh else en

    fun clamp(value: String, max: Int): String = value.trim().take(max)

    fun normalizeEmail(email: String): String = email.trim().lowercase()

    fun isValidEmail(email: String): Boolean {
        val e = normalizeEmail(email)
        return e.isNotEmpty() && e.length <= EMAIL_MAX && emailRegex.matches(e)
    }

    fun requireEmail(email: String): String? {
        val e = normalizeEmail(email)
        if (e.isEmpty()) {
            return msg("Email is required", "ត្រូវការអ៊ីមែល")
        }
        if (!isValidEmail(e)) {
            return msg("Enter a valid email address", "បញ្ចូលអ៊ីមែលឱ្យត្រឹមត្រូវ")
        }
        return null
    }

    fun requirePassword(password: String, labelEn: String = "Password", labelKh: String = "ពាក្យសម្ងាត់"): String? {
        if (password.isEmpty()) {
            return msg("$labelEn is required", "ត្រូវការ$labelKh")
        }
        if (password.length < PASSWORD_MIN) {
            return msg(
                "$labelEn must be at least $PASSWORD_MIN characters",
                "$labelKh ត្រូវមានយ៉ាងហោច $PASSWORD_MIN តួ",
            )
        }
        if (password.length > PASSWORD_MAX) {
            return msg(
                "$labelEn must be at most $PASSWORD_MAX characters",
                "$labelKh មិនលើស $PASSWORD_MAX តួ",
            )
        }
        return null
    }

    fun requireName(name: String): String? {
        val n = name.trim()
        if (n.isEmpty()) return msg("Name is required", "ត្រូវការឈ្មោះ")
        if (n.length < NAME_MIN) {
            return msg("Name is too short", "ឈ្មោះខ្លីពេក")
        }
        if (n.length > NAME_MAX) {
            return msg("Name is too long", "ឈ្មោះវែងពេក")
        }
        if (emailRegex.matches(n)) {
            return msg("Name cannot be an email address", "ឈ្មោះមិនអាចជាអ៊ីមែល")
        }
        return null
    }

    fun requireCompany(company: String): String? {
        val c = company.trim()
        if (c.length < 2) {
            return msg("Enter your company name", "បញ្ចូលឈ្មោះក្រុមហ៊ុន")
        }
        if (c.length > COMPANY_MAX) {
            return msg("Company name is too long", "ឈ្មោះក្រុមហ៊ុនវែងពេក")
        }
        return null
    }

    fun passwordsMatch(a: String, b: String): String? {
        if (a != b) return msg("Passwords do not match", "ពាក្យសម្ងាត់មិនត្រូវគ្នា")
        return null
    }

    fun requireDifferentPasswords(current: String, next: String): String? {
        if (current == next) {
            return msg(
                "New password must be different from current password",
                "ពាក្យសម្ងាត់ថ្មីត្រូវខុសពីបច្ចុប្បន្ន",
            )
        }
        return null
    }

    fun requirePriceUsd(raw: String): String? {
        val n = raw.trim().toDoubleOrNull()
            ?: return msg("Price (USD) is required", "ត្រូវការតម្លៃ (USD)")
        if (n < 0) return msg("Price cannot be negative", "តម្លៃមិនអាចអវិជ្ជមាន")
        if (n > PRICE_MAX) {
            return msg("Price is too high", "តម្លៃខ្ពស់ពេក")
        }
        return null
    }

    fun requireTourTitle(title: String): String? {
        val t = title.trim()
        if (t.isEmpty()) return msg("Title is required", "ត្រូវការចំណងជើង")
        if (t.length > TITLE_MAX) return msg("Title is too long", "ចំណងជើងវែងពេក")
        return null
    }

    fun requireTourDescription(description: String): String? {
        val d = description.trim()
        if (d.isEmpty()) return msg("Description is required", "ត្រូវការពិពណ៌នា")
        if (d.length > DESCRIPTION_MAX) {
            return msg("Description is too long", "ពិពណ៌នាវែងពេក")
        }
        return null
    }

    fun requireLocation(location: String): String? {
        val l = location.trim()
        if (l.isEmpty()) return msg("Location is required", "ត្រូវការទីតាំង")
        if (l.length > LOCATION_MAX) return msg("Location is too long", "ទីតាំងវែងពេក")
        return null
    }

    fun requireSeats(raw: String): String? {
        val n = raw.trim().toIntOrNull()
            ?: return msg("Seats are required", "ត្រូវការកៅអី")
        if (n < SEATS_MIN || n > SEATS_MAX) {
            return msg(
                "Seats must be between $SEATS_MIN and $SEATS_MAX",
                "កៅអីត្រូវនៅចន្លោះ $SEATS_MIN–$SEATS_MAX",
            )
        }
        return null
    }

    fun requireStars(stars: Int): String? {
        if (stars !in 1..5) {
            return msg("Rating must be 1 to 5 stars", "ការវាយតម្លៃត្រូវ ១ ទៅ ៥ ផ្កាយ")
        }
        return null
    }

    fun requireResetToken(token: String): String? {
        if (token.trim().isEmpty()) {
            return msg("Reset code is required", "ត្រូវការកូដកំណត់ឡើងវិញ")
        }
        return null
    }

    fun requirePlaceTitle(title: String): String? {
        val t = title.trim()
        if (t.isEmpty()) return msg("Place name is required", "ត្រូវការឈ្មោះកន្លែង")
        if (t.length > TITLE_MAX) return msg("Place name is too long", "ឈ្មោះកន្លែងវែងពេក")
        return null
    }

    fun validateLogin(email: String, password: String): String? =
        requireEmail(email) ?: requirePassword(password)

    fun validateRegister(
        name: String,
        email: String,
        password: String,
        isBusiness: Boolean,
        companyName: String,
    ): String? =
        requireName(name)
            ?: requireEmail(email)
            ?: requirePassword(password)
            ?: if (isBusiness) requireCompany(companyName) else null

    fun validateTourPost(
        title: String,
        description: String,
        priceUsd: String,
        isTransport: Boolean,
        locationOrArea: String,
        seats: String,
    ): String? =
        requireTourTitle(title)
            ?: requireTourDescription(description)
            ?: requirePriceUsd(priceUsd)
            ?: requireLocation(locationOrArea)
            ?: if (isTransport) requireSeats(seats) else null
}
