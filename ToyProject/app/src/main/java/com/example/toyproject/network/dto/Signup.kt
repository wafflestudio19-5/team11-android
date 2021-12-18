package com.example.toyproject.network.dto

data class Signup(
    val user_id : String,
    val password : String,
    val email : String,
    val admission_year : Int,
    val nickname : String
)