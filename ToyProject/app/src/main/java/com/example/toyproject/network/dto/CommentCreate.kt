package com.example.toyproject.network.dto

// 댓글 쓸 때 서버에 발신&서버에서 수신하는 dto
data class CommentCreate (
    val parent : Int,
    val text : String,
    val is_anonymous : Boolean
)

data class CommentCreateResponse (
    val success : Boolean,
    val comment_id : Int
)

// 댓글 및 게시글 좋아요 결과 response
data class LikeResponse (
    val like : Int?,
    val error : String?,
    val detail : String?
)

// 게시글 스크랩 결과 response
data class ScrapResponse (
    val scrap : Int?,
    val detail : String?,
    val error : String?
)

// 댓글 삭제 결과 response
data class CommentDeleteResponse(
    val success : Boolean?
)