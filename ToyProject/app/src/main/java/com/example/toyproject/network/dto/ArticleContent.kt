package com.example.toyproject.network.dto



// ArticleActivity 에서 개별 게시글을 불러올 때 사용하는 dto
data class ArticleContent (
    val id : Int,
    val title : String,
    val text : String,
    val user_nickname : String,
    val like_count : Int,
    val scrap_count : Int,
    val comment_count : Int,
    val image_count : Int,
    val created_at : String,
    val f_created_at : String,
    val comments : MutableList<Comment>
)

data class Comment(
    val id : Int,
    val parent : Int,
    val is_subcomment : Boolean,
    val is_mine : Boolean,
    val text : String,
    val created_at : String,
    val like_count : Int,
    val is_writer : Boolean,
    val user_nickname: String
)