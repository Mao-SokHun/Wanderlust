package com.example.wanderlust.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.friendlyNetworkMessage
import com.example.wanderlust.data.model.ImageUploadResponse
import com.example.wanderlust.data.remote.ApiConnection
import com.example.wanderlust.data.remote.WanderlustApi
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MediaUploadRepository {

    suspend fun uploadImages(context: Context, uris: List<Uri>): Result<List<String>> {
        if (uris.isEmpty()) return Result.success(emptyList())
        val header = SessionManager.authHeader()
            ?: return Result.failure(Exception("Please sign in first"))
        return withContext(Dispatchers.IO) {
            try {
                val api = uploadApi()
                val parts = uris.mapIndexed { index, uri ->
                    val bytes = compressImage(context, uri)
                        ?: return@withContext Result.failure(Exception("Could not read image ${index + 1}"))
                    val body = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images", "photo_$index.jpg", body)
                }
                // Upload in batches of 10 (server limit)
                val allUrls = mutableListOf<String>()
                for (chunk in parts.chunked(10)) {
                    val response: ImageUploadResponse = api.uploadBusinessImages(header, chunk)
                    allUrls += response.urls.filter { it.isNotBlank() }
                }
                Result.success(allUrls)
            } catch (e: Exception) {
                Result.failure(Exception(friendlyNetworkMessage(e)))
            }
        }
    }

    private suspend fun uploadApi(): WanderlustApi {
        val base = ApiConnection.api().let { ApiConnection.activeUrl() }
            ?: run {
                ApiConnection.api()
                ApiConnection.activeUrl()
            }
            ?: throw IllegalStateException("No API URL")
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(180, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        return Retrofit.Builder()
            .baseUrl(if (base.endsWith("/")) base else "$base/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WanderlustApi::class.java)
    }

    private fun compressImage(context: Context, uri: Uri): ByteArray? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        val maxSide = 1600
        val w = bounds.outWidth.coerceAtLeast(1)
        val h = bounds.outHeight.coerceAtLeast(1)
        while (w / sample > maxSide || h / sample > maxSide) {
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        } ?: return null
        return try {
            val out = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 82, out)
            out.toByteArray()
        } finally {
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    }
}
