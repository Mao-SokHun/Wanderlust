package com.example.wanderlust.data

import android.content.Context
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
    var userLanguage: String = "en"
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
    }

    fun setThemeDark(dark: Boolean) {
        userThemeDark = dark
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
        userLanguage = "en"
        // Theme is device-local (Settings toggle); do not overwrite from API sync.
        userNotificationsEnabled = profile.notificationsEnabled
        userLocationEnabled = profile.locationEnabled
        persistSession()
    }

    fun authHeader(): String? = token?.let { "Bearer $it" }

    fun clear() {
        token = null
        userId = null
        userName = null
        userEmail = null
        userRole = null
        userBio = ""
        userLanguage = "en"
        userThemeDark = false
        userNotificationsEnabled = true
        userLocationEnabled = true
        appContext?.let { AuthPreferences.clear(it) }
    }

    fun restoreFromDisk() {
        val saved = appContext?.let { AuthPreferences.load(it) } ?: return
        token = saved.token
        userId = saved.userId
        userName = saved.name
        userEmail = saved.email
        userRole = saved.role
        userBio = saved.bio
        userLanguage = saved.language
        userThemeDark = saved.themeDark
        userNotificationsEnabled = saved.notificationsEnabled
        userLocationEnabled = saved.locationEnabled
    }

    fun isAdmin(): Boolean = userRole == "ADMIN"

    fun updateName(name: String) {
        userName = name
        persistSession()
    }

    fun currentProfileUpdateRequest(): com.example.wanderlust.data.model.ProfileUpdateRequest =
        com.example.wanderlust.data.model.ProfileUpdateRequest(
            name = userName.orEmpty(),
            bio = userBio,
            language = "en",
            themeDark = userThemeDark,
            notificationsEnabled = userNotificationsEnabled,
            locationEnabled = userLocationEnabled,
        )

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
            userLanguage,
            userThemeDark,
            userNotificationsEnabled,
            userLocationEnabled,
        )
    }
}
