package com.example.toyproject.network.dto

data class CreateArticle(
    val success: Boolean?,
    val article_id: Int?,
    val error: String?,
    val detail: String?
)
