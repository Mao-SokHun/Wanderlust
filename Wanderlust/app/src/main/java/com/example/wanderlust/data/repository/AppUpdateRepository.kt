package com.example.wanderlust.data.repository

import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.model.AppVersionInfo
import com.example.wanderlust.data.remote.ApiConnection

data class AppUpdateAvailability(
    val info: AppVersionInfo,
    val downloadUrl: String,
    val forceUpdate: Boolean,
)

class AppUpdateRepository {

    suspend fun checkForUpdate(): Result<AppUpdateAvailability?> = runCatching {
        val info = ApiConnection.api().getAppVersion()
        val current = BuildConfig.VERSION_CODE
        if (info.versionCode <= current) return@runCatching null

        val force = info.forceUpdate || current < info.minSupportedVersionCode
        val rawUrl = info.androidDownloadUrl.ifBlank { info.downloadUrl }.trim()
        if (rawUrl.isEmpty()) return@runCatching null

        val absolute = when {
            rawUrl.startsWith("http://") || rawUrl.startsWith("https://") -> rawUrl
            else -> {
                val base = ApiConnection.activeUrl()?.trimEnd('/') ?: return@runCatching null
                "$base/${rawUrl.trimStart('/')}"
            }
        }
        AppUpdateAvailability(
            info = info,
            downloadUrl = absolute,
            forceUpdate = force,
        )
    }

    companion object {
        fun installedVersionLabel(): String =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}
