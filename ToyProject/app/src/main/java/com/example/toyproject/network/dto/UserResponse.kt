package com.example.toyproject.network.dto

// /my/ response
data class UserResponse(
    val user_id : String?,
    val name : String?,
    val email: String?,
    val admission_year: Int?,
    val nickname : String?,
    val university : String?,
)