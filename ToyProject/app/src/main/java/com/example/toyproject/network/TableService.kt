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


    @POST("/api/v1/schedule/")
    suspend fun createSchedule(@Body param : ScheduleCreate) : DefaultSchedule

    @POST("/api/v1/schedule/default/custom_lecture/")
    suspend fun addCustomLectureToDefault(@Body param : UserMadeLectureAdd) : CustomLecture

    @POST("/api/v1/schedule/default/custom_lecture/")
    suspend fun addCustomLectureServer(@Body param : AddCustomLectureById) : CustomLecture

    @DELETE("/api/v1/schedule/default/custom_lecture/{custom_lecture_id}/")
    suspend fun deleteCustomLectureFromDefault(
        @Path("custom_lecture_id") custom_lecture_id : Int
    )

    @PUT("/api/v1/schedule/default/custom_lecture/{custom_lecture_id}/")
    suspend fun editCustomLectureFromDefault(
        @Body param : EditCustomLecture,
        @Path("custom_lecture_id") custom_lecture_id : Int
    ) : CustomLecture

    @PUT("/api/v1/schedule/default/custom_lecture/{custom_lecture_id}/")
    suspend fun editLectureMemoNick(
        @Body param : EditCustomLecture,
        @Path("custom_lecture_id") custom_lecture_id : Int
    ) : CustomLecture

    @GET("/api/v1/schedule/default/lecture/")
    suspend fun loadServerLectures(
        @Query("offset") offset : Int = 20,
        @Query("limit") limit : Int = 20,
        @Query("subject_name") subject_name : String? =null,
        @Query("professor") professor : String? =null,
        @Query("subject_code") subject_code : String? =null,
        @Query("location") location : String? = null,
        @Query("department") department : String? = null,
        @Query("grade") grade : Int? = null,
        @Query("credit") credit : Int? = null,
        @Query("category") category : String? = null
    ) : ServerLectureList

    @GET("/api/v1/schedule/")
    suspend fun getSchedules() : ScheduleListGet

    @POST("/api/v1/schedule/")
    suspend fun makeSchedule(
        @Body param : ScheduleCreate
    ) : Schedule

    @GET("/api/v1/schedule/{schedule_id}/custom_lecture/")
    suspend fun loadLecturesById(
        @Path("schedule_id") scheduleId : Int
    ) : DefaultScheduleLectureList
}