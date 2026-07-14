package com.example.wanderlust

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.example.wanderlust.BuildConfig
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.local.DbProvider
import com.example.wanderlust.data.remote.ApiConnection
import com.example.wanderlust.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WanderlustApp : Application(), ImageLoaderFactory {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        DbProvider.init(this)
        ApiConnection.init(this)
        val mapsKey = BuildConfig.MAPS_API_KEY
        if (mapsKey.isNotBlank() && !com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initializeWithNewPlacesApiEnabled(this, mapsKey)
        }
        appScope.launch {
            runCatching { ApiConnection.api() }
            if (SessionManager.isLoggedIn()) {
                runCatching { AuthRepository().fetchProfile() }
            }
        }
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
}
