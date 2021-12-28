package com.example.toyproject.network.dto

// /my/ response
data class UserResponse(
    val id : String?,
    val name : String?,
    val email: String?,
    val nickname : String?,
    val university : String?,
    val admissionYear : Int?
)