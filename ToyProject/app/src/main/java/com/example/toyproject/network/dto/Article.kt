package com.example.toyproject.network.dto

data class Article(
    val id: Int,
    val title: String,
    val text: String,
    val user_nickname: String,
    val like_count: Int,
    val comment_count: Int,
    val image_count: Int,
    val created_at: String,
    val f_created_at: String
)
