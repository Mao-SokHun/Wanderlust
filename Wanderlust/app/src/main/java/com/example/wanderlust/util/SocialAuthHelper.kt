package com.example.wanderlust.util

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.wanderlust.BuildConfig
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

sealed class SocialAuthResult {
    data class Google(val idToken: String) : SocialAuthResult()
    data class Facebook(val accessToken: String) : SocialAuthResult()
    data class Error(val message: String) : SocialAuthResult()
}

object SocialAuthHelper {
    val facebookCallbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

    fun googleConfigured(): Boolean = BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

    fun facebookConfigured(): Boolean =
        BuildConfig.FACEBOOK_APP_ID.isNotBlank() &&
            BuildConfig.FACEBOOK_APP_ID != "0" &&
            BuildConfig.FACEBOOK_CLIENT_TOKEN.isNotBlank() &&
            BuildConfig.FACEBOOK_CLIENT_TOKEN != "0"

    suspend fun signInWithGoogle(context: Context): SocialAuthResult {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()
        if (clientId.isEmpty()) {
            return SocialAuthResult.Error(
                "Add GOOGLE_WEB_CLIENT_ID to local.properties (Web client ID from Google Cloud / Firebase).",
            )
        }
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .setNonce(UUID.randomUUID().toString())
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val response = CredentialManager.create(context).getCredential(
                request = request,
                context = context,
            )
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                val token = google.idToken
                if (token.isNullOrBlank()) {
                    SocialAuthResult.Error("Google did not return an ID token.")
                } else {
                    SocialAuthResult.Google(token)
                }
            } else {
                SocialAuthResult.Error("Unexpected Google credential type.")
            }
        } catch (e: GetCredentialException) {
            SocialAuthResult.Error(e.message ?: "Google sign-in cancelled")
        } catch (e: Exception) {
            SocialAuthResult.Error(e.message ?: "Google sign-in failed")
        }
    }

    suspend fun signInWithFacebook(activity: Activity): SocialAuthResult {
        if (!facebookConfigured()) {
            return SocialAuthResult.Error(
                "Add FACEBOOK_APP_ID and FACEBOOK_CLIENT_TOKEN to local.properties.",
            )
        }
        val existing = AccessToken.getCurrentAccessToken()
        if (existing != null && !existing.isExpired) {
            return SocialAuthResult.Facebook(existing.token)
        }
        return suspendCancellableCoroutine { cont ->
            val loginManager = LoginManager.getInstance()
            loginManager.logOut()
            loginManager.registerCallback(
                facebookCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        loginManager.unregisterCallback(facebookCallbackManager)
                        val token = result.accessToken?.token
                        if (token.isNullOrBlank()) {
                            cont.resume(SocialAuthResult.Error("Facebook did not return a token."))
                        } else {
                            cont.resume(SocialAuthResult.Facebook(token))
                        }
                    }

                    override fun onCancel() {
                        loginManager.unregisterCallback(facebookCallbackManager)
                        cont.resume(SocialAuthResult.Error("Facebook sign-in cancelled"))
                    }

                    override fun onError(error: FacebookException) {
                        loginManager.unregisterCallback(facebookCallbackManager)
                        cont.resume(SocialAuthResult.Error(error.message ?: "Facebook sign-in failed"))
                    }
                },
            )
            cont.invokeOnCancellation {
                loginManager.unregisterCallback(facebookCallbackManager)
            }
            loginManager.logInWithReadPermissions(activity, listOf("email", "public_profile"))
        }
    }
}
