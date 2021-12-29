package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @GET("/api/v1/my/")
    fun getUserProfile(): Call<UserResponse>

    @PUT("/api/v1/my/password/")
    fun changePassword(@Body password : Password) : Call<ChangeSuccess>

    @PUT("api/v1/my/email/")
    fun changeEmail(@Body email : Email) : Call<ChangeSuccess>

    @PUT("api/v1/my/nickname/")
    fun changeNick(@Body nickname : Nickname) : Call<ChangeSuccess>

    //탈퇴는 아직 구현하지 않음.
}

data class Password(
    val password: String
)

data class Email(
    val email: String
)

data class Nickname(
    val nickname: String
)

data class ChangeSuccess(
    val success: Boolean?,
    val detail: String?
)