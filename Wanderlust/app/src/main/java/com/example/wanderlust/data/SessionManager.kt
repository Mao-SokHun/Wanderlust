package com.example.wanderlust.data

import android.content.Context
import com.example.wanderlust.data.model.ProfileUpdateRequest
import com.example.wanderlust.data.model.UserProfile

/**
 * In-memory session; profile fields persisted via [AuthPreferences] (synced from API/DB).
 */
object SessionManager {
    private var appContext: Context? = null

    var token: String? = null
        private set
    var userId: String? = null
        private set
    var userName: String? = null
        private set
    var userEmail: String? = null
        private set
    var userRole: String? = null
        private set
    var userBio: String = ""
        private set
    var userPhone: String = ""
        private set
    var userCity: String = ""
        private set
    var userGender: String = ""
        private set
    var userBirthDate: String = ""
        private set
    var userNationality: String = "Cambodia"
        private set
    var userTravelStyle: String = ""
        private set
    var userEmergencyContact: String = ""
        private set
    var userLanguage: String = "km"
        private set
    var userThemeDark: Boolean = false
        private set
    var userNotificationsEnabled: Boolean = true
        private set
    var userLocationEnabled: Boolean = true
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        AuthPreferences.migrateDefaultLightTheme(context)
        restoreFromDisk()
        // Language applies for guests too (default km).
        val lang = AuthPreferences.loadLanguage(context)
        userLanguage = lang
        com.example.wanderlust.locale.AppLocale.set(lang)
    }

    fun setThemeDark(dark: Boolean) {
        userThemeDark = dark
        persistSession()
        persistLanguageAndTheme()
    }

    fun setLanguage(raw: String) {
        val lang = com.example.wanderlust.locale.AppLocale.normalize(raw)
        userLanguage = lang
        com.example.wanderlust.locale.AppLocale.set(lang)
        appContext?.let { AuthPreferences.saveLanguage(it, lang) }
        persistSession()
    }

    fun isLoggedIn(): Boolean = !token.isNullOrBlank()

    fun saveLogin(
        token: String,
        name: String,
        role: String,
        userId: String? = null,
        email: String? = null,
    ) {
        this.token = token
        this.userId = userId
        userName = name
        userEmail = email
        userRole = role
        persistSession()
    }

    fun applyProfile(profile: UserProfile) {
        userId = profile.id
        userName = profile.name
        userEmail = profile.email
        userRole = profile.role
        userBio = profile.bio
        userPhone = profile.phone
        userCity = profile.city
        userGender = profile.gender
        userBirthDate = profile.birthDate
        userNationality = profile.nationality.ifBlank { "Cambodia" }
        userTravelStyle = profile.travelStyle
        userEmergencyContact = profile.emergencyContact
        userLanguage = com.example.wanderlust.locale.AppLocale.normalize(profile.language)
        com.example.wanderlust.locale.AppLocale.set(userLanguage)
        // Theme is device-local (Settings toggle); do not overwrite from API sync.
        userNotificationsEnabled = profile.notificationsEnabled
        userLocationEnabled = profile.locationEnabled
        persistSession()
        persistLanguageAndTheme()
    }

    fun authHeader(): String? = token?.let { "Bearer $it" }

    fun clear() {
        val keepLanguage = userLanguage.ifBlank { "km" }
        token = null
        userId = null
        userName = null
        userEmail = null
        userRole = null
        userBio = ""
        userPhone = ""
        userCity = ""
        userGender = ""
        userBirthDate = ""
        userNationality = "Cambodia"
        userTravelStyle = ""
        userEmergencyContact = ""
        userLanguage = keepLanguage
        userThemeDark = false
        userNotificationsEnabled = true
        userLocationEnabled = true
        appContext?.let {
            AuthPreferences.clear(it)
            AuthPreferences.saveLanguage(it, keepLanguage)
        }
        com.example.wanderlust.locale.AppLocale.set(keepLanguage)
    }

    fun restoreFromDisk() {
        val saved = appContext?.let { AuthPreferences.load(it) }
        if (saved == null) {
            val lang = appContext?.let { AuthPreferences.loadLanguage(it) } ?: "km"
            userLanguage = lang
            com.example.wanderlust.locale.AppLocale.set(lang)
            return
        }
        token = saved.token
        userId = saved.userId
        userName = saved.name
        userEmail = saved.email
        userRole = saved.role
        userBio = saved.bio
        userPhone = saved.phone
        userCity = saved.city
        userGender = saved.gender
        userBirthDate = saved.birthDate
        userNationality = saved.nationality
        userTravelStyle = saved.travelStyle
        userEmergencyContact = saved.emergencyContact
        userLanguage = com.example.wanderlust.locale.AppLocale.normalize(saved.language)
        userThemeDark = saved.themeDark
        userNotificationsEnabled = saved.notificationsEnabled
        userLocationEnabled = saved.locationEnabled
        com.example.wanderlust.locale.AppLocale.set(userLanguage)
    }

    fun isAdmin(): Boolean = userRole == "ADMIN"

    fun updateName(name: String) {
        userName = name
        persistSession()
    }

    fun currentProfileUpdateRequest(): ProfileUpdateRequest =
        ProfileUpdateRequest(
            name = userName.orEmpty(),
            bio = userBio,
            phone = userPhone,
            city = userCity,
            gender = userGender,
            birthDate = userBirthDate,
            nationality = userNationality,
            travelStyle = userTravelStyle,
            emergencyContact = userEmergencyContact,
            language = com.example.wanderlust.locale.AppLocale.normalize(userLanguage),
            themeDark = userThemeDark,
            notificationsEnabled = userNotificationsEnabled,
            locationEnabled = userLocationEnabled,
        )

    private fun persistLanguageAndTheme() {
        val ctx = appContext ?: return
        AuthPreferences.saveLanguage(ctx, userLanguage)
        // Theme is also in session save when logged in; guests only keep language prefs.
        if (!isLoggedIn()) return
        persistSession()
    }

    private fun persistSession() {
        val ctx = appContext ?: return
        val t = token ?: return
        val id = userId ?: return
        val name = userName ?: return
        val email = userEmail ?: return
        val role = userRole ?: return
        AuthPreferences.save(
            ctx,
            t,
            id,
            name,
            email,
            role,
            userBio,
            userPhone,
            userCity,
            userGender,
            userBirthDate,
            userNationality,
            userTravelStyle,
            userEmergencyContact,
            userLanguage,
            userThemeDark,
            userNotificationsEnabled,
            userLocationEnabled,
        )
    }
}
