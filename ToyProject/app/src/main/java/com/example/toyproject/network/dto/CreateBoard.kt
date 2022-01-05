package com.example.toyproject.network.dto

data class CreateBoard(
    val id: Int?,
    val name: String?,
    val university: String?,
    val type: Int?,
    val description: String?,
    val allow_anonymous: Boolean?,
    val error : String?,
    val detail: String?
)
