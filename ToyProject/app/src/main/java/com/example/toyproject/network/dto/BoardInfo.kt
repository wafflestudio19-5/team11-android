package com.example.toyproject.network.dto

data class BoardInfo(
    val id: Int,
    val name: String,
    val university: String,
    val type: Int,
    val description: String?,
    val allow_anonymous: Boolean,
    val is_mine: Boolean,
    val favorite: Boolean
)
