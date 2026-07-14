package com.example.wanderlust.data.repository

import com.example.wanderlust.data.friendlyNetworkMessage
import com.example.wanderlust.data.remote.ApiConnection
import com.example.wanderlust.data.remote.WanderlustApi

internal suspend fun <T> apiCall(block: suspend (WanderlustApi) -> T): Result<T> {
    return try {
        Result.success(block(ApiConnection.api()))
    } catch (first: Exception) {
        ApiConnection.clearCache()
        try {
            Result.success(block(ApiConnection.api(forceRediscover = true)))
        } catch (second: Exception) {
            val hint = ApiConnection.activeUrl()?.let { " Last tried: $it" }.orEmpty()
            Result.failure(Exception(friendlyNetworkMessage(second) + hint))
        }
    }
}
