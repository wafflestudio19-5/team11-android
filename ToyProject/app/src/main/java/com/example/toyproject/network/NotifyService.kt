package com.example.toyproject.network

import com.example.toyproject.network.dto.NotificationResponse
import com.example.toyproject.network.dto.Success
import retrofit2.http.GET
import retrofit2.http.Path

interface NotifyService {

    @GET("/api/v1/notification/")
    suspend fun getNotification(

    ): NotificationResponse

    @GET("/api/v1/notification/{notification_id}")
    suspend fun readNotification(
        @Path("notification_id") notification_id: Int
    ): Success

}