package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import okhttp3.RequestBody
import okhttp3.MultipartBody
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

    @Multipart
    @POST("/api/v1/board/{board_id}/article/")
    suspend fun createArticle(
        @Path("board_id") board_id: Int,
        @PartMap data: HashMap<String, RequestBody>,
        @Part texts : MutableList<MultipartBody.Part>,
        @Part images: MutableList<MultipartBody.Part>
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

    @POST("api/v1/subscribe/article/{article_id}/")
    fun subscribeArticle(
        @Path("article_id") article_id: Int
    ) : Call<ScrapResponse>

    @POST("api/v1/subscribe/comment/{comment_id}/")
    fun subscribeComment(
        @Path("comment_id") comment_id: Int
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
    fun deleteArticle(
        @Path("board_id") board_id: Int,
        @Path("article_id") article_id: Int
    ) : Call<Success>


    @GET("/api/v1/board_favorite/")
    suspend fun searchFavoriteBoard(): FetchAllBoard

    @PUT("/api/v1/board_favorite/{board_id}/")
    suspend fun putFavoriteBoard(
        @Path("board_id") board_id: Int
    ) : FavoriteBoard

    @FormUrlEncoded
    @POST("/api/v1/message/")
    suspend fun sendMessage(
        @Field("article_id") article_id: Int,
        @Field("comment_id") comment_id: Int,
        @Field("text") text: String
    ) : Success



}