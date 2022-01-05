package com.example.toyproject.network.dto

data class MyArticle(
    val id: Int,
    val board_id: Int,
    val title: String,
    val text: String,
    val user_nickname: String,
    val is_mine: Boolean,
    val like_count: Int,
    val scrap_count: Int,
    val comment_count: Int,
    val image_count: Int,
    val created_at: String,
    val f_created_at: String,
    val has_scraped: Boolean,
    val has_liked: Boolean
)
