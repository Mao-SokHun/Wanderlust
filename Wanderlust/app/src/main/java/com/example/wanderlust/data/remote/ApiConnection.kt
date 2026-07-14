package com.example.wanderlust.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.util.concurrent.TimeUnit

/**
 * Picks the first working base URL (USB, emulator, or Wi‑Fi) and caches it for the session.
 */
object ApiConnection {

    @Volatile
    private var cachedApi: WanderlustApi? = null

    @Volatile
    private var activeBaseUrl: String? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(6, TimeUnit.SECONDS)
        .build()

    suspend fun api(forceRediscover: Boolean = false): WanderlustApi {
        if (!forceRediscover) {
            cachedApi?.let { return it }
        }
        return withContext(Dispatchers.IO) {
            val baseUrl = discoverBaseUrlInternal(forceRediscover)
            activeBaseUrl = baseUrl
            buildApi(baseUrl).also { cachedApi = it }
        }
    }

    fun activeUrl(): String? = activeBaseUrl

    fun clearCache() {
        cachedApi = null
        activeBaseUrl = null
    }

    private suspend fun discoverBaseUrlInternal(force: Boolean): String {
        if (!force) {
            activeBaseUrl?.let { url ->
                if (probeUrl(url)) return url
            }
        }
        val found = coroutineScope {
            ApiConstants.CANDIDATE_BASE_URLS.map { url ->
                async { url.takeIf { probeUrl(it) } }
            }.awaitAll().firstOrNull { it != null }
        }
        return found ?: throw ConnectException(
            "No server on port ${ApiConstants.PORT}. Start backend (npm start). " +
                "USB: adb reverse tcp:3000 tcp:3000. Wi‑Fi: set WIFI_PC_IP in ApiConstants.kt",
        )
    }

    private suspend fun probeUrl(baseUrl: String): Boolean = try {
        withTimeout(2_500) {
            buildApi(baseUrl).health()
        }
        true
    } catch (_: Exception) {
        false
    }

    private fun buildApi(baseUrl: String): WanderlustApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WanderlustApi::class.java)
}
