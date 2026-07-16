package com.example.wanderlust.locale

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.data.DestinationCard

/**
 * App UI language. Backend stores `en` or `km` (Khmer).
 * Default is Khmer (`km`).
 */
object AppLocale {
    const val EN = "en"
    const val KM = "km"

    /** Observed by Compose — changing this recomposes localized UI. */
    var code by mutableStateOf(KM)
        private set

    val isKhmer: Boolean get() = code == KM

    fun normalize(raw: String?): String {
        val v = raw.orEmpty().trim().lowercase()
        return when (v) {
            "km", "kh", "kmr", "khm", "khmer" -> KM
            "en", "eng", "english" -> EN
            else -> KM
        }
    }

    fun set(raw: String) {
        code = normalize(raw)
    }
}

@Composable
fun stringLocalized(@StringRes enId: Int, @StringRes kmId: Int): String {
    // Read [AppLocale.code] so language switches recompose.
    val lang = AppLocale.code
    return stringResource(if (lang == AppLocale.EN) enId else kmId)
}

/**
 * Prefer `name_kh` when UI language is Khmer; otherwise English [id].
 * Use this instead of [stringResource] for user-visible copy.
 */
@Composable
fun stringApp(@StringRes id: Int): String {
    val lang = AppLocale.code
    val context = LocalContext.current
    if (lang == AppLocale.EN) return stringResource(id)
    val kmId = remember(id) {
        val name = context.resources.getResourceEntryName(id)
        if (name.endsWith("_kh")) id
        else context.resources.getIdentifier("${name}_kh", "string", context.packageName)
    }
    return if (kmId != 0) stringResource(kmId) else stringResource(id)
}

@Composable
fun stringApp(@StringRes id: Int, vararg formatArgs: Any): String {
    val lang = AppLocale.code
    val context = LocalContext.current
    if (lang == AppLocale.EN) return stringResource(id, *formatArgs)
    val kmId = remember(id) {
        val name = context.resources.getResourceEntryName(id)
        if (name.endsWith("_kh")) id
        else context.resources.getIdentifier("${name}_kh", "string", context.packageName)
    }
    return if (kmId != 0) stringResource(kmId, *formatArgs) else stringResource(id, *formatArgs)
}

/**
 * Non-Compose: toasts, ViewModels, Intents. Honors [AppLocale.code] + `_kh` resources.
 */
fun Context.stringApp(@StringRes id: Int, vararg formatArgs: Any): String {
    val lang = AppLocale.code
    if (lang == AppLocale.EN) {
        return if (formatArgs.isEmpty()) getString(id) else getString(id, *formatArgs)
    }
    val name = resources.getResourceEntryName(id)
    val kmId =
        if (name.endsWith("_kh")) id
        else resources.getIdentifier("${name}_kh", "string", packageName)
    return when {
        kmId == 0 && formatArgs.isEmpty() -> getString(id)
        kmId == 0 -> getString(id, *formatArgs)
        formatArgs.isEmpty() -> getString(kmId)
        else -> getString(kmId, *formatArgs)
    }
}

@Composable
fun DestinationCard.localizedTitle(): String {
    val lang = AppLocale.code
    return if (lang == AppLocale.KM && titleKh.isNotBlank()) titleKh else title
}

@Composable
fun DestinationCard.localizedLocation(): String {
    val lang = AppLocale.code
    return if (lang == AppLocale.KM && locationKh.isNotBlank()) locationKh else location
}

@Composable
fun DestinationCard.localizedDescription(): String {
    val lang = AppLocale.code
    return if (lang == AppLocale.KM && descriptionKh.isNotBlank()) descriptionKh else description
}

@Composable
fun DestinationCard.localizedCategory(): String {
    val lang = AppLocale.code
    return if (lang == AppLocale.KM && categoryKh.isNotBlank()) categoryKh else category
}
