package com.example.toyproject.network

import com.example.toyproject.network.dto.Detail
import com.example.toyproject.network.dto.RegisterCheck
import retrofit2.http.GET

interface Service {
    @GET("/api/v1/signup/")
    suspend fun getStatusCode(): Detail

    @GET("/api/v1/register/check_id")
    fun checkId(user_id : String) : RegisterCheck

    @GET("/api/v1/register/check_email")
    fun checkEmail(user_id : String) : RegisterCheck

    @GET("/api/v1/register/check_nickname")
    fun checkNickname(user_id : String) : RegisterCheck

}