package com.example.toyproject.network.dto

data class Message(
    val id: Int,
    val type: String,
    val created_at: String,
    val text: String
)

data class MessageRoom(
    val id: Int,
    val user_nickname: String,
    val unread_count: Int,
    val last_message: LastMessage
)

data class LastMessage(
    val id: Int,
    val created_at: String,
    val text: String
)


data class MessageResponse(
    val id: Int,
    val user_nickname: String,
    val messages: MutableList<Message>
)

data class MessageRoomResponse(
    val has_new_message: Boolean,
    val message_rooms: MutableList<MessageRoom>
)