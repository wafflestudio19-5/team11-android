package com.example.toyproject.network

import com.example.toyproject.network.dto.Detail
import retrofit2.http.GET

interface Service {
    @GET("/api/v1/signup/")
    suspend fun getStatusCode(): Detail

}