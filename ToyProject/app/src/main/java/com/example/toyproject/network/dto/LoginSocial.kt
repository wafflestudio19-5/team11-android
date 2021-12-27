package com.example.toyproject.network.dto

// 소셜 로그인 관련 data class 들
// Register 할 때 & 응답, Login 할때 & 응답

data class LoginSocial(
    val access_token : String
)

data class LoginSocialResponse(
    val success : String?,
    val token : String?,
    val non_field_error : String?,
    val access_token : String?
)


data class RegisterSocial(
    val access_token: String?,
    val name : String?,
    val email : String?,
    val university : String?,
    val admission_year : Int?,
)

data class RegisterSocialResponse(
    val email : String,
    val token : String
)