package com.example.toyproject.network

import com.example.toyproject.network.dto.ArticleContent
import com.example.toyproject.network.dto.Board
import com.example.toyproject.network.dto.FetchAllBoard
import com.example.toyproject.network.dto.FetchArticlesByBoard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

}