package com.example.toyproject.network.dto

import okhttp3.MultipartBody

data class Signup(
    val user_id : String,
    val password : String,
    val email : String,
    val admission_year : Int,
    val nickname : String,
    val university: String?,
    val name : String,
    val profile_image: MultipartBody.Part?
)

