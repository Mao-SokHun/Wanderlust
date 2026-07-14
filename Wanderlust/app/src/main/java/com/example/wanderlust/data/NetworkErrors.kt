package com.example.wanderlust.data

import com.example.wanderlust.data.model.MessageResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private val errorGson = Gson()

fun friendlyNetworkMessage(throwable: Throwable): String {
    if (throwable is HttpException) {
        parseApiErrorMessage(throwable)?.let { return it }
        return when (throwable.code()) {
            401 -> "Wrong email or password. Use test: user@test.com / 123456"
            403 -> "Access denied"
            400 -> "Invalid request"
            404 -> "Not found"
            409 -> "This email is already registered. Please sign in."
            500, 503 -> "Server error — check backend logs and database"
            else -> "Request failed (${throwable.code()})"
        }
    }
    return when (throwable) {
        is ConnectException,
        is UnknownHostException,
        is SocketTimeoutException,
        -> "Cannot reach server. Start backend (npm start) and check USB/Wi-Fi connection."
        else -> {
            val raw = throwable.message.orEmpty()
            when {
                raw.contains("Failed to connect", ignoreCase = true) ->
                    "Cannot reach server. Start backend: cd backend → npm start"
                raw.contains("CLEARTEXT", ignoreCase = true) ->
                    "HTTP blocked. Rebuild app after network_security_config update."
                raw.startsWith("HTTP ") ->
                    parseHttpStatusLine(raw) ?: raw
                else -> raw.ifBlank { "Something went wrong" }
            }
        }
    }
}

private fun parseApiErrorMessage(http: HttpException): String? {
    val body = http.response()?.errorBody()?.string().orEmpty()
    if (body.isBlank()) return null
    return try {
        errorGson.fromJson(body, MessageResponse::class.java).message?.takeIf { it.isNotBlank() }
    } catch (_: Exception) {
        null
    }
}

private fun parseHttpStatusLine(raw: String): String? {
    val code = Regex("HTTP (\\d+)").find(raw)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: return null
    return when (code) {
        401 -> "Wrong email or password. Use test: user@test.com / 123456"
        else -> null
    }
}
