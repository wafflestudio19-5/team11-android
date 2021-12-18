package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface Service {
    @GET("/api/v1/signup/")
    suspend fun getStatusCode(): Detail

    @GET("/api/v1/register/check_id")
    fun checkId(user_id : String) : RegisterCheck

    @GET("/api/v1/register/check_email")
    fun checkEmail(user_id : String) : RegisterCheck

    @GET("/api/v1/register/check_nickname")
    fun checkNickname(user_id : String) : RegisterCheck

    @GET("api/v1/register/university")
    suspend fun getUniversityList() : List<University>

    @POST("api/v1/register/")
    fun register(param : Signup) : Call<SignupResponse>

    @POST("api/v1/login/")
    fun login(param : Login) : Call<SignupResponse>

}