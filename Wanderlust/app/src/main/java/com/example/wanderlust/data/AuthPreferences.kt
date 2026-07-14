package com.example.wanderlust.data

import android.content.Context
import com.example.wanderlust.data.model.UserProfile

/** Persists login session and user profile cache (synced from PostgreSQL). */
object AuthPreferences {
    private const val PREFS = "wanderlust_auth"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"
    private const val KEY_BIO = "bio"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_THEME_DARK = "theme_dark"
    private const val KEY_THEME_LIGHT_DEFAULT_MIGRATED = "theme_light_default_migrated_v1"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"
    private const val KEY_LOCATION = "location_enabled"

    /** One-time: older builds defaulted to dark; reset stored preference to light. */
    fun migrateDefaultLightTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_THEME_LIGHT_DEFAULT_MIGRATED, false)) return
        prefs.edit()
            .putBoolean(KEY_THEME_DARK, false)
            .putBoolean(KEY_THEME_LIGHT_DEFAULT_MIGRATED, true)
            .apply()
    }

    data class SavedSession(
        val token: String,
        val userId: String,
        val name: String,
        val email: String,
        val role: String,
        val bio: String = "",
        val language: String = "en",
        val themeDark: Boolean = false,
        val notificationsEnabled: Boolean = true,
        val locationEnabled: Boolean = true,
    )

    fun save(
        context: Context,
        token: String,
        userId: String,
        name: String,
        email: String,
        role: String,
        bio: String = "",
        language: String = "en",
        themeDark: Boolean = false,
        notificationsEnabled: Boolean = true,
        locationEnabled: Boolean = true,
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .putString(KEY_BIO, bio)
            .putString(KEY_LANGUAGE, language)
            .putBoolean(KEY_THEME_DARK, themeDark)
            .putBoolean(KEY_NOTIFICATIONS, notificationsEnabled)
            .putBoolean(KEY_LOCATION, locationEnabled)
            .apply()
    }

    fun saveProfile(context: Context, profile: UserProfile, token: String) {
        save(
            context = context,
            token = token,
            userId = profile.id,
            name = profile.name,
            email = profile.email,
            role = profile.role,
            bio = profile.bio,
            language = profile.language,
            themeDark = profile.themeDark,
            notificationsEnabled = profile.notificationsEnabled,
            locationEnabled = profile.locationEnabled,
        )
    }

    fun load(context: Context): SavedSession? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val role = prefs.getString(KEY_ROLE, null) ?: return null
        return SavedSession(
            token = token,
            userId = userId,
            name = name,
            email = email,
            role = role,
            bio = prefs.getString(KEY_BIO, "").orEmpty(),
            language = prefs.getString(KEY_LANGUAGE, "en") ?: "en",
            themeDark = prefs.getBoolean(KEY_THEME_DARK, false),
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS, true),
            locationEnabled = prefs.getBoolean(KEY_LOCATION, true),
        )
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
