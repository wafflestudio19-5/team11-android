package com.example.toyproject.network.dto


// Signup, Login 할 때 서버로부터의 응답
data class SignupResponse(
    val user_id : String?,
    val token : String?,
    val error : String?,
    val detail: String?
)
