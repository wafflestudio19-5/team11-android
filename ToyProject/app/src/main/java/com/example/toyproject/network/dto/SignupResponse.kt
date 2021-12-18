package com.example.toyproject.network.dto

data class SignupResponse(
    val user_id : String?,
    val token : String?,
    val error : String?,
    val detail: String?
)