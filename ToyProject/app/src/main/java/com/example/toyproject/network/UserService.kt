package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @GET("/api/v1/my/")
    fun getUserProfile(): Call<UserResponse>

    @PUT("/api/v1/my/password/")
    fun changePassword(@Query("password") password : String) : Call<ChangeSuccess>

    @PUT("api/v1/my/email/")
    fun changeEmail(@Query("email") email : String) : Call<ChangeSuccess>

    @PUT("api/v1/my/nickname/")
    fun changeNick(@Query("nickname") nickname : String) : Call<ChangeSuccess>

    //탈퇴는 아직 구현하지 않음.
}

data class ChangeSuccess(
    val success: Boolean,
    val detail: String?
)