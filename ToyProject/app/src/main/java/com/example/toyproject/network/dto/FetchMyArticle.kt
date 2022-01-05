package com.example.toyproject.network.dto

data class FetchMyArticle(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: MutableList<MyArticle>?
)
