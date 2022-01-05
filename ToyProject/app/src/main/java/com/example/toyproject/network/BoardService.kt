package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.http.*

interface BoardService {

    @GET("/api/v1/board/")
    suspend fun getBoardList() : FetchAllBoard

    @GET("/api/v1/board/{board_id}/article/")
    suspend fun getArticlesByBoard(
        @Path("board_id") board_id: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): FetchArticlesByBoard

    @FormUrlEncoded
    @POST("/api/v1/board/")
    suspend fun createBoard(
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("allow_anonymous") allow_anonymous: Boolean,
        @Field("type") type: Int
    ): CreateBoard

    @FormUrlEncoded
    @POST("/api/v1/board/{board_id}/article/")
    suspend fun createArticle(
        @Path("board_id") board_id: Int,
        @Field("title") title: String,
        @Field("text") text: String,
        @Field("is_anonymous") is_anonymous: Boolean,
        @Field("is_question") is_question: Boolean
    ): CreateArticle

    @GET("/api/v1/board/")
    suspend fun searchBoard(
        @Query("search") search: String
    ): FetchAllBoard

    @GET("/api/v1/board/{board_id}/article/")
    suspend fun getArticlesByKeyword(
        @Path("board_id") board_id: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ) : FetchArticlesByBoard
}