package com.example.toyproject.network.dto

// /my/ response
data class UserResponse(
    val user_id : String?,
    val name : String?,
    val email: String?,
    val nickname : String?,
    val university : String?,
    val admission_year: Int?,
    val is_active: Boolean?,
    val profile_image: String?
)