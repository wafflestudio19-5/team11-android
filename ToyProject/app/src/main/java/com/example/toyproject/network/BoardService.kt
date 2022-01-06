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

    @GET("/api/v1/board/{board_id}/")
    suspend fun getBoardInfo(
        @Path("board_id") board_id: Int
    ): BoardInfo

    @GET("/api/v1/board/{board_id}/article/")
    suspend fun getArticlesByKeyword(
        @Path("board_id") board_id: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ) : FetchArticlesByBoard

    @GET("/api/v1/board/my/article/")
    suspend fun getMyArticle(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("interest") interest: String
    ) : FetchMyArticle

    @GET("/api/v1/board/all/article/")
    suspend fun getHotBestArticle(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("interest") interest: String
    ) : FetchMyArticle

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
    fun likeComment(
        @Path("comment_id") comment_id : Int
    ) : Call<LikeResponse>

    @POST("api/v1/like/article/{article_id}/")
    fun likeArticle(
        @Path("article_id") article_id : Int
    ) : Call<LikeResponse>

    @POST("api/v1/scrap/article/{article_id}/")
    fun scrapArticle(
        @Path("article_id") article_id: Int
    ) : Call<ScrapResponse>

    @DELETE("/api/v1/board/{board_id}/article/{article_id}/comment/{comment_id}/")
    fun deleteComment(
        @Path("board_id") board_id : Int,
        @Path("article_id") article_id : Int,
        @Path("comment_id") comment_id: Int
    ) : Call<CommentDeleteResponse>

    @DELETE("api/v1/board/{board_id}/")
    suspend fun deleteBoard(
        @Path("board_id") board_id: Int
    ) : Success

    @DELETE("api/v1/board/{board_id}/article/{article_id}/")
    suspend fun deleteArticle(
        @Path("board_id") board_id: Int,
        @Path("article_id") article_id: Int
    ) : Success

    @GET("/api/v1/board_favorite/")
    suspend fun searchFavoriteBoard(): FetchAllBoard

}