package com.example.toyproject.network

import androidx.room.Delete
import com.example.toyproject.network.dto.table.*
import retrofit2.Call
import retrofit2.http.*

interface TableService {

    @GET("/api/v1/schedule/default/")
    suspend fun getDefaultSchedule() : DefaultSchedule

    @GET("/api/v1/schedule/default/custom_lecture/")
    suspend fun getDefaultScheduleLectures() : DefaultScheduleLectureList

    @GET("/api/v1/schedule/default/lecture/")

    @POST("/api/v1/schedule/")
    suspend fun createSchedule(@Body param : ScheduleCreate) : DefaultSchedule

    @POST("/api/v1/schedule/default/custom_lecture/")
    suspend fun addCustomLectureToDefault(@Body param : UserMadeLectureAdd) : CustomLecture

    @DELETE("/api/v1/schedule/default/custom_lecture/{custom_lecture_id}/")
    suspend fun deleteCustomLectureFromDefault(
        @Path("custom_lecture_id") custom_lecture_id : Int
    )

    @PUT("/api/v1/schedule/default/custom_lecture/{custom_lecture_id}/")
    suspend fun editCustomLectureFromDefault(
        @Body param : EditCustomLecture,
        @Path("custom_lecture_id") custom_lecture_id : Int
    ) : CustomLecture

    @GET("/api/v1/schedule/")
    suspend fun getSchedules() : ScheduleListGet

    @POST("/api/v1/schedule/")
    suspend fun makeSchedule(
        @Body param : ScheduleCreate
    ) : Schedule
}