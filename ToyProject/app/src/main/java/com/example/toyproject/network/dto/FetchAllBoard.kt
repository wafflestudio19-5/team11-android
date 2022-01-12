package com.example.toyproject.network.dto

data class FetchAllBoard(
    val boards: List<Board>?,
    val error: String?,
    val detail: String?
)
