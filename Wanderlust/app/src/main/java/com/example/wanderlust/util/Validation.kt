package com.example.wanderlust.util

import com.example.wanderlust.locale.AppLocale
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    const val PHONE_DIGITS_MIN = 8
    const val PHONE_DIGITS_MAX = 15
    const val COMMENT_MAX = 500
    const val PRICE_MAX = 100_000.0
    const val SEATS_MIN = 1
    const val SEATS_MAX = 60
    const val URL_MAX = 800
    const val IMAGE_URLS_MAX = 40
    const val MAKE_MODEL_MAX = 80
    const val CONDITION_MAX = 400
    const val ACTIVITY_MAX = 200
    const val TELEGRAM_MAX = 120
    const val CITY_MAX = 100
    const val SUPPORT_MESSAGE_MIN = 10
    const val SUPPORT_MESSAGE_MAX = 1200
    const val DAYS_MIN = 1
    const val DAYS_MAX = 21

    private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private val isoDateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    private val timeHmRegex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
    private val hexTokenRegex = Regex("^[a-fA-F0-9]{8}$")
    private val urlRegex = Regex("^https?://\\S+$", RegexOption.IGNORE_CASE)
    private val phoneCleanRegex = Regex("[\\s\\-().]")

    private val isoDateFmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

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

    /** Empty OK; if filled must be a valid email. */
    fun optionalEmail(email: String): String? {
        if (email.trim().isEmpty()) return null
        return requireEmail(email)
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

    /** Empty OK; if filled must be a valid price. */
    fun optionalPriceUsd(raw: String): String? {
        if (raw.trim().isEmpty()) return null
        return requirePriceUsd(raw)
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

    fun optionalSeats(raw: String): String? {
        if (raw.trim().isEmpty()) return null
        return requireSeats(raw)
    }

    fun requireStars(stars: Int): String? {
        if (stars !in 1..5) {
            return msg("Rating must be 1 to 5 stars", "ការវាយតម្លៃត្រូវ ១ ទៅ ៥ ផ្កាយ")
        }
        return null
    }

    fun requireResetToken(token: String): String? {
        val t = token.trim()
        if (t.isEmpty()) {
            return msg("Reset code is required", "ត្រូវការកូដកំណត់ឡើងវិញ")
        }
        if (!hexTokenRegex.matches(t)) {
            return msg("Enter the 8-character reset code", "បញ្ចូលកូដ ៨ តួ")
        }
        return null
    }

    fun requirePlaceTitle(title: String): String? {
        val t = title.trim()
        if (t.isEmpty()) return msg("Place name is required", "ត្រូវការឈ្មោះកន្លែង")
        if (t.length > TITLE_MAX) return msg("Place name is too long", "ឈ្មោះកន្លែងវែងពេក")
        return null
    }

    fun requireMakeModel(value: String): String? {
        val v = value.trim()
        if (v.length < 2) {
            return msg("Enter make & model", "បញ្ចូលម៉ាក និងម៉ូដែល")
        }
        if (v.length > MAKE_MODEL_MAX) {
            return msg("Make & model is too long", "ម៉ាក/ម៉ូដែលវែងពេក")
        }
        return null
    }

    fun requireDays(raw: String): String? {
        val n = raw.trim().toIntOrNull()
            ?: return msg("Days are required", "ត្រូវការចំនួនថ្ងៃ")
        if (n < DAYS_MIN || n > DAYS_MAX) {
            return msg(
                "Days must be between $DAYS_MIN and $DAYS_MAX",
                "ថ្ងៃត្រូវនៅចន្លោះ $DAYS_MIN–$DAYS_MAX",
            )
        }
        return null
    }

    fun optionalNights(raw: String): String? {
        if (raw.trim().isEmpty()) return null
        val n = raw.trim().toIntOrNull()
            ?: return msg("Nights must be a number", "យប់ត្រូវជាលេខ")
        if (n < 0 || n > DAYS_MAX) {
            return msg("Nights must be between 0 and $DAYS_MAX", "យប់ត្រូវនៅចន្លោះ ០–$DAYS_MAX")
        }
        return null
    }

    /**
     * @param required if true, empty fails
     * @param notInPast if true, date must be today or later
     */
    fun requireIsoDate(
        raw: String,
        required: Boolean = true,
        notInPast: Boolean = false,
        labelEn: String = "Date",
        labelKh: String = "ថ្ងៃ",
    ): String? {
        val v = raw.trim()
        if (v.isEmpty()) {
            return if (required) msg("$labelEn is required (YYYY-MM-DD)", "ត្រូវការ$labelKh (YYYY-MM-DD)")
            else null
        }
        if (!isoDateRegex.matches(v)) {
            return msg("Use date format YYYY-MM-DD", "ប្រើទ្រង់ទ្រាយ YYYY-MM-DD")
        }
        val parsed = try {
            LocalDate.parse(v, isoDateFmt)
        } catch (_: DateTimeParseException) {
            return msg("Invalid date", "ថ្ងៃមិនត្រឹមត្រូវ")
        }
        if (notInPast && parsed.isBefore(LocalDate.now())) {
            return msg("$labelEn cannot be in the past", "$labelKh មិនអាចជាអតីតកាល")
        }
        return null
    }

    fun requireTimeHm(
        raw: String,
        required: Boolean = true,
        labelEn: String = "Time",
        labelKh: String = "ម៉ោង",
    ): String? {
        val v = raw.trim()
        if (v.isEmpty()) {
            return if (required) msg("$labelEn is required (HH:mm)", "ត្រូវការ$labelKh (HH:mm)")
            else null
        }
        if (!timeHmRegex.matches(v)) {
            return msg("Use time format HH:mm (e.g. 07:30)", "ប្រើទ្រង់ទ្រាយ HH:mm (ឧ. 07:30)")
        }
        return null
    }

    fun optionalPhone(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return null
        if (v.length > PHONE_MAX) {
            return msg("Phone number is too long", "លេខទូរស័ព្ទវែងពេក")
        }
        val digits = v.replace(phoneCleanRegex, "").removePrefix("+")
        if (!digits.all { it.isDigit() } || digits.length !in PHONE_DIGITS_MIN..PHONE_DIGITS_MAX) {
            return msg(
                "Enter a valid phone ($PHONE_DIGITS_MIN–$PHONE_DIGITS_MAX digits)",
                "បញ្ចូលលេខទូរស័ព្ទត្រឹមត្រូវ ($PHONE_DIGITS_MIN–$PHONE_DIGITS_MAX ខ្ទង់)",
            )
        }
        return null
    }

    fun optionalUrl(raw: String, max: Int = URL_MAX): String? {
        val v = raw.trim()
        if (v.isEmpty()) return null
        if (v.length > max) return msg("Link is too long", "តំណវែងពេក")
        if (!urlRegex.matches(v)) {
            return msg("Link must start with http:// or https://", "តំណត្រូវចាប់ផ្ដើម http:// ឬ https://")
        }
        return null
    }

    fun requireContactChannel(phone: String, telegram: String, messenger: String = ""): String? {
        val hasPhone = phone.trim().isNotEmpty()
        val hasTg = telegram.trim().isNotEmpty()
        val hasMsg = messenger.trim().isNotEmpty()
        if (!hasPhone && !hasTg && !hasMsg) {
            return msg(
                "Add at least one contact (phone or Telegram)",
                "បន្ថែមទំនាក់ទំនងយ៉ាងហោចមួយ (ទូរស័ព្ទ ឬ Telegram)",
            )
        }
        return optionalPhone(phone)
            ?: if (hasTg && telegram.trim().length > TELEGRAM_MAX) {
                msg("Telegram is too long", "Telegram វែងពេក")
            } else null
    }

    fun optionalBirthDate(raw: String): String? {
        val v = raw.trim()
        if (v.isEmpty()) return null
        val formatErr = requireIsoDate(v, required = true, notInPast = false, labelEn = "Birth date", labelKh = "ថ្ងៃកំណើត")
        if (formatErr != null) return formatErr
        val parsed = LocalDate.parse(v, isoDateFmt)
        if (parsed.isAfter(LocalDate.now())) {
            return msg("Birth date cannot be in the future", "ថ្ងៃកំណើតមិនអាចជាអនាគត")
        }
        val age = LocalDate.now().year - parsed.year
        if (age > 120) {
            return msg("Birth date looks invalid", "ថ្ងៃកំណើតមិនត្រឹមត្រូវ")
        }
        return null
    }

    fun requireSupportMessage(message: String): String? {
        val m = message.trim()
        if (m.length < SUPPORT_MESSAGE_MIN) {
            return msg(
                "Describe the problem (at least $SUPPORT_MESSAGE_MIN characters)",
                "ពិពណ៌នាបញ្ហា (យ៉ាងហោច $SUPPORT_MESSAGE_MIN តួ)",
            )
        }
        if (m.length > SUPPORT_MESSAGE_MAX) {
            return msg("Message is too long", "សារវែងពេក")
        }
        return null
    }

    fun requireDifferentCities(a: String, b: String): String? {
        if (a.trim().equals(b.trim(), ignoreCase = true)) {
            return msg("Departure and arrival must differ", "ចេញ និងដល់ ត្រូវខុសគ្នា")
        }
        return null
    }

    fun requireUntilAfterTravel(travelDate: String, untilDate: String): String? {
        val travelErr = requireIsoDate(travelDate, required = true, labelEn = "Travel date", labelKh = "ថ្ងៃធ្វើដំណើរ")
        if (travelErr != null) return travelErr
        val untilErr = requireIsoDate(untilDate, required = true, labelEn = "Until date", labelKh = "ថ្ងៃបញ្ចប់")
        if (untilErr != null) return untilErr
        val travel = LocalDate.parse(travelDate.trim(), isoDateFmt)
        val until = LocalDate.parse(untilDate.trim(), isoDateFmt)
        if (!until.isAfter(travel)) {
            return msg("Until date must be after travel date", "ថ្ងៃបញ្ចប់ត្រូវក្រោយថ្ងៃធ្វើដំណើរ")
        }
        return null
    }

    fun validateLogin(email: String, password: String): String? =
        requireEmail(email) ?: requirePassword(password)

    fun validateRegister(
        name: String,
        email: String,
        password: String,
        confirmPassword: String = password,
        isBusiness: Boolean,
        companyName: String,
    ): String? =
        requireName(name)
            ?: requireEmail(email)
            ?: requirePassword(password)
            ?: passwordsMatch(password, confirmPassword)
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
