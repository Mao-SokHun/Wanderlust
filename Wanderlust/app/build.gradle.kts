import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY", "")
val googleWebClientId: String = localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")
val facebookAppId: String = localProperties.getProperty("FACEBOOK_APP_ID", "")
val facebookClientToken: String = localProperties.getProperty("FACEBOOK_CLIENT_TOKEN", "")

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
val hasReleaseKeystore =
    keystoreProperties["storeFile"] != null &&
        rootProject.file(keystoreProperties["storeFile"].toString()).exists()

android {
    namespace = "com.example.wanderlust"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.wanderlust"
        minSdk = 26
        targetSdk = 35
        // Bump BOTH when shipping an update (see backend/APP_UPDATE.md).
        // Keep APP_VERSION_CODE / APP_VERSION_NAME on the API in sync.
        versionCode = 5
        versionName = "1.2.2"
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
        buildConfigField("String", "FACEBOOK_APP_ID", "\"$facebookAppId\"")
        buildConfigField("String", "FACEBOOK_CLIENT_TOKEN", "\"$facebookClientToken\"")
        resValue("string", "facebook_app_id", facebookAppId.ifBlank { "0" })
        resValue("string", "facebook_client_token", facebookClientToken.ifBlank { "0" })
        resValue("string", "fb_login_protocol_scheme", "fb${facebookAppId.ifBlank { "0" }}")
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        manifestPlaceholders["FACEBOOK_APP_ID"] = facebookAppId.ifBlank { "0" }
        manifestPlaceholders["FACEBOOK_CLIENT_TOKEN"] = facebookClientToken.ifBlank { "0" }
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties["storeFile"].toString())
                storePassword = keystoreProperties["storePassword"].toString()
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                // JAR (v1) + APK Signature Scheme v2 — some phones reject v2-only APKs.
                isV1SigningEnabled = true
                isV2SigningEnabled = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    lint {
        // Avoid blocking release builds when IDE/Gradle lock lint cache on Windows.
        checkReleaseBuilds = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.zxing.core)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.maps.compose)
    implementation(libs.places)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)
    implementation(libs.facebook.login)
    implementation("com.google.android.material:material:1.12.0")
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation("androidx.compose.ui:ui-tooling")
}
