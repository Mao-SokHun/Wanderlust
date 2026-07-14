package com.example.wanderlust.data.model

data class AppVersionInfo(
    val versionCode: Int = 1,
    val versionName: String = "1.0",
    val minSupportedVersionCode: Int = 1,
    val forceUpdate: Boolean = false,
    val downloadUrl: String = "",
    val releaseNotes: String = "",
    val androidDownloadUrl: String = "",
    val iosDownloadUrl: String = "",
    val androidAvailable: Boolean = true,
    val iosAvailable: Boolean = false,
    val downloadPageUrl: String = "",
)
