package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @GET("/api/v1/my/")
    fun getUserProfile(): Call<UserResponse>

    @PUT("/api/v1/my/password/")
    fun changePassword(@Body password : Password) : Call<ChangeSuccess>

    @PUT("/api/v1/my/email/")
    fun changeEmail(@Body email : Email) : Call<ChangeSuccess>

    @POST("/api/v1/email_code/")
    fun sendEmailCode(@Query("email") email: String): Call<Void>

    @GET("/api/v1/email_code/")
    fun compareCode(@Query("email") email: String, @Query("code") code: Int): Call<CompareResult>

    @PUT("/api/v1/my/nickname/")
    fun changeNick(@Body nickname : Nickname) : Call<ChangeSuccess>

    @DELETE("/api/v1/my/withdrawal/")
    fun withdrawal(@Query("password") password: String): Call<WithdrawalSuccess>
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
)

data class WithdrawalSuccess(
    val success: Boolean?,
)

data class CompareResult(
    val Result: String?
)