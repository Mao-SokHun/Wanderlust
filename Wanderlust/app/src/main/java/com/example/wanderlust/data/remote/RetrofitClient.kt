package com.example.wanderlust.data.remote

/**
 * Use [ApiConnection.api] for calls — supports USB, emulator, and Wi‑Fi automatically.
 */
object RetrofitClient {

    @Deprecated("Use ApiConnection.api() for multi-network support")
    val api: WanderlustApi
        get() = throw UnsupportedOperationException("Use ApiConnection.api() instead")
}
