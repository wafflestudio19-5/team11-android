package com.example.toyproject.network.dto

data class FetchArticlesByBoard(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: MutableList<Article>?
)
