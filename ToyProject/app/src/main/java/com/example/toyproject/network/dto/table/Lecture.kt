package com.example.toyproject.network.dto.table

data class Lecture (
    val id : Int,
    val subject_name : String,
    val professor : String?,
    val subject_code : String,
    val year : Int,
    val season : Int,
    val college : String,
    val department : String,
    val grade : Int,
    val level : String,
    val credit : Int,
    val category : String,
    val number : Int,
    val detail : String,
    val language : String,
    val time : String,
    val location : String,
    val subject_professor : Int
)