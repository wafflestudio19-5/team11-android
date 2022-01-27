package com.example.toyproject.network

import com.example.toyproject.network.dto.*
import retrofit2.http.*

interface NoteService {

    @FormUrlEncoded
    @POST("/api/v1/message_room/{message_room_id}/message/")
    suspend fun sendMessage(
        @Path("message_room_id") message_room_id: Int,
        @Field("text") text: String
    ) : ErrorMessage

    @GET("/api/v1/message_room/{message_room_id}/")
    suspend fun getMessage(
        @Path("message_room_id") message_room_id: Int
    ) : MessageResponse

    @GET("/api/v1/message_room/")
    suspend fun getMessageRoom() : MessageRoomResponse

    @FormUrlEncoded
    @POST("/api/v1/message/")
    suspend fun sendStartCommentMessage(
        @Field("comment_id") comment_id: Int,
        @Field("text") text: String
    ) : Success

    @FormUrlEncoded
    @POST("/api/v1/message/")
    suspend fun sendStartArticleMessage(
        @Field("article_id") article_id: Int,
        @Field("text") text: String
    ) : Success



}