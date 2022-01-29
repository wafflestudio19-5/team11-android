package com.example.toyproject.network.dto

data class Notification(
    val id: Int,
    val board_id: Int,
    val board_name: String,
    val article_id: Int,
    val unread: Boolean,
    val created_at: String,
    val text: String
)

data class NotificationResponse(
    val count: String,
    val next: String?,
    val previous: String?,
    val results: MutableList<Notification>
)

data class UnreadCount(
    val unread_count: Int
)
