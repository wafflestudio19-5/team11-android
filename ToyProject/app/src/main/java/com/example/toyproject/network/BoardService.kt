package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.Call
import retrofit2.http.*

interface BoardService {

    @GET("/api/v1/board/")
    suspend fun getBoardList() : FetchAllBoard

    @GET("/api/v1/board/{board_id}/article/")
    suspend fun getArticlesByBoard(
        @Path("board_id") board_id: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): FetchArticlesByBoard

    @GET("api/v1/board/{board_id}/article/{article_id}/")
    suspend fun getArticleContent(
        @Path("board_id") board_id : Int,
        @Path("article_id") article_id : Int,
    ) : ArticleContent

    @POST("api/v1/board/{board_id}/article/{article_id}/comment/")
    suspend fun addComment(
        @Path("board_id") board_id : Int,
        @Path("article_id") article_id : Int,
        @Body param : CommentCreate
    ) : CommentCreateResponse

    @POST("api/v1/like/comment/{comment_id}/")
    suspend fun likeComment(
        @Path("comment_id") comment_id : Int
    ) : LikeResponse

    @POST("api/v1/like/article/{article_id}/")
    fun likeArticle(
        @Path("article_id") article_id : Int
    ) : Call<LikeResponse>

    @POST("api/v1/scrap/article/{article_id}/")
    fun scrapArticle(
        @Path("article_id") article_id: Int
    ) : Call<ScrapResponse>

    @DELETE("api/v1/board/{board_id}/article/{article_id}/comment/{comment_id}/")
    fun deleteComment(
        @Path("board_id") board_id : Int,
        @Path("article_id") article_id : Int,
        @Path("comment_id") comment_id: Int
    ) : Call<CommentDeleteResponse>
}