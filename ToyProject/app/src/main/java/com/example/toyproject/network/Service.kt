package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import okhttp3.RequestBody
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Service {
    @GET("/api/v1/signup/")
    suspend fun getStatusCode(): Detail

    @GET("/api/v1/register/check_id/")
    fun checkId(@Query("user_id") user_id : String) : Call<RegisterCheck>

    @GET("/api/v1/register/check_email/")
    fun checkEmail(@Query("email") email : String) : Call<RegisterCheck>

    @GET("/api/v1/register/check_nickname/")
    fun checkNickname(@Query("nickname") nickname : String) : Call<RegisterCheck>

    @GET("api/v1/register/university/")
    suspend fun getUniversityList() : GetUniversity

    @Multipart
    @POST("api/v1/register/")
    fun register(@PartMap data: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?) : Call<SignupResponse>

    @POST("api/v1/login/")
    fun login(@Body param : Login) : Call<SignupResponse>

    @POST("api/v1/login/")
    fun loginToken() : Call<SignupResponse>

    @POST("api/v1/register/fire/")
    fun googleRegister(@Body param : RegisterSocial) : Call<RegisterSocialResponse>

    @POST("api/v1/login/fire/")
    fun googleLogin(@Body param : LoginSocial) : Call<LoginSocialResponse>

    @POST("api/v1/register/kakao/")
    fun kakaoRegister(@Body param : RegisterSocial) : Call<RegisterSocialResponse>

    @POST("api/v1/login/kakao/")
    fun kakaoLogin(@Body param : LoginSocial) : Call<LoginSocialResponse>

    @FormUrlEncoded
    @POST("api/v1/fcm_token/")
    suspend fun fcmToken(
        @Field("fcm_token") fcm_token : String
    ) : Success



}
