package com.example.wanderlust.data.repository

import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.model.AppSupportInfo
import com.example.wanderlust.data.model.MessageResponse
import com.example.wanderlust.data.model.SupportContactRequest

class SupportRepository {

    suspend fun getSupportInfo(): Result<AppSupportInfo> =
        apiCall { it.getAppSupport() }

    suspend fun sendMessage(
        topic: String,
        message: String,
        replyEmail: String = "",
        replyPhone: String = "",
    ): Result<MessageResponse> {
        val header = SessionManager.authHeader()
        return apiCall {
            it.sendSupportContact(
                token = header,
                request = SupportContactRequest(
                    topic = topic,
                    message = message,
                    replyEmail = replyEmail,
                    replyPhone = replyPhone,
                ),
            )
        }
    }
}
