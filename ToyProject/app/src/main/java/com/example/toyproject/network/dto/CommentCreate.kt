package com.example.toyproject.network.dto

data class CommentCreate (
    val parent : Int,
    val text : String,
    val is_anonymous : Boolean
)

data class CommentCreateResponse (
    val success : Boolean,
    val comment_id : Int
)