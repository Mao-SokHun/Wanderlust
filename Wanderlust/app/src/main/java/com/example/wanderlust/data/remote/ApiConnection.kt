package com.example.wanderlust.data.remote

import android.content.Context
import java.net.ConnectException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Picks the first working base URL (USB, emulator, Wi‑Fi, or Render) and caches it.
 * Discovery races candidates — first healthy response wins (does not wait for slow/cold hosts).
 */
object ApiConnection {

    private const val PREFS = "wanderlust_api"
    private const val KEY_LAST_URL = "last_base_url"
    private const val PROBE_TIMEOUT_MS = 1_200L

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var cachedApi: WanderlustApi? = null

    @Volatile
    private var activeBaseUrl: String? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    fun init(context: Context) {
        appContext = context.applicationContext
        val saved = appContext
            ?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            ?.getString(KEY_LAST_URL, null)
        if (!saved.isNullOrBlank()) {
            activeBaseUrl = saved
        }
    }

    suspend fun api(forceRediscover: Boolean = false): WanderlustApi {
        if (!forceRediscover) {
            cachedApi?.let { return it }
        }
        return withContext(Dispatchers.IO) {
            val baseUrl = discoverBaseUrlInternal(forceRediscover)
            activeBaseUrl = baseUrl
            persistUrl(baseUrl)
            buildApi(baseUrl).also { cachedApi = it }
        }
    }

    fun activeUrl(): String? = activeBaseUrl

    fun clearCache() {
        cachedApi = null
        // Keep last URL hint for faster rediscovery
    }

    private fun persistUrl(url: String) {
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_LAST_URL, url)
            ?.apply()
    }

    private fun candidateOrder(force: Boolean): List<String> {
        val last = activeBaseUrl
        val base = ApiConstants.CANDIDATE_BASE_URLS
        return if (!force && !last.isNullOrBlank()) {
            listOf(last) + base.filter { it != last }
        } else {
            base
        }
    }

    private suspend fun discoverBaseUrlInternal(force: Boolean): String {
        // Fast path: last known URL still alive
        if (!force) {
            activeBaseUrl?.let { url ->
                if (probeUrl(url)) return url
            }
        }

        val winner = raceFirstHealthy(candidateOrder(force))
        if (winner != null) return winner

        throw ConnectException(
            "No server on port ${ApiConstants.PORT}. Start backend (npm start). " +
                "USB: adb reverse tcp:3000 tcp:3000. Wi‑Fi: set WIFI_PC_IP in ApiConstants.kt",
        )
    }

    private suspend fun probeUrl(baseUrl: String): Boolean = try {
        val timeout = if (baseUrl.startsWith("https://")) 8_000L else PROBE_TIMEOUT_MS
        withTimeoutOrNull(timeout) {
            buildApi(baseUrl).health()
            true
        } == true
    } catch (_: Exception) {
        false
    }

    /** First probe that succeeds wins; slower candidates are cancelled. */
    private suspend fun raceFirstHealthy(urls: List<String>): String? = coroutineScope {
        val done = CompletableDeferred<String>()
        val jobs = urls.map { url ->
            launch {
                if (probeUrl(url)) {
                    done.complete(url)
                }
            }
        }
        try {
            // Allow Render cold start to finish while local probes fail quickly.
            withTimeoutOrNull(10_000L) {
                done.await()
            }
        } finally {
            jobs.forEach { it.cancel() }
        }
    }

    private fun buildApi(baseUrl: String): WanderlustApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WanderlustApi::class.java)
}
