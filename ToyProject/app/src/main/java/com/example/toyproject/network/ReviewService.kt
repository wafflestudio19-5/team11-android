package com.example.toyproject.network

import retrofit2.Call
import retrofit2.http.*

interface ReviewService {
    @GET("/api/v1/subject_professor/{subject_professor_id}/")
    fun getLectureInfo(
        @Path("subject_professor_id") id: Int
    ): Call<LectureInfo>

    @GET("/api/v1/subject_professor/{subject_professor_id}/review/")
    fun getReviewList(
        @Path("subject_professor_id") id: Int,
    ): Call<ReviewList>

    @POST("/api/v1/subject_professor/{subject_professor_id}/review/")
    fun postReview(
        @Path("subject_professor_id") id: Int,
        @Body param: CreateReview
    ): Call<Review>

    @GET("/api/v1/subject_professor/{subject_professor_id}/information/")
    fun getInformation(
        @Path("subject_professor_id") id: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<InformationList>

    @POST("/api/v1/subject_professor/{subject_professor_id}/information/")
    fun postInformation(
        @Path("subject_professor_id") id: Int,
        @Body param: CreateInformation
    ): Call<Information>
}

data class LectureInfo(
    val id: Int,
    val subject_name: String,
    val professor: String,
    val review: ReviewAvg?,
    val semester: List<String>
)

data class ReviewAvg(
    val homework: String,
    val team_activity: String,
    val grading: String,
    val attendance: String,
    val test_count: Int,
    val rating: Int
)

data class ReviewList(
    val reviews: MutableList<Review>
)

data class Review(
    val id: Int,
    val subject_professor_id: Int,
    val year: Int,
    val season: String,
    //val homework: String,
   // val team_activity: String,
    //val grading: String,
    //val attendance: String,
    //val test_count: String,
    val rating: Int,
    val comment: String,
    //val subject_professor: Int,
    //val user: Int
)

data class ReviewResponse(
    val id: Int,
    val subject_professor_id: Int,
    val year: Int,
    val season: String,
    val homework: String,
    val team_activity: String,
    val grading: String,
    val attendance: String,
    val test_count: String,
    val rating: Int,
    val comment: String,
    val subject_professor: Int,
    val user: Int
)

data class CreateReview(
    val rating: Int,
    val homework: Int,
    val team_activity: Int,
    val grading : Int,
    val attendance : Int,
    val test_count : Int,
    val comment: String,
    val year: Int,
    val season: Int
)

data class InformationList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: MutableList<Information>?
)

data class Information(
    val id: Int,
    val subject_professor_id: Int,
    val year: Int,
    val season: String,
    val test_type: Int,
    val test_method: String,
    val strategy: String,
    val problems: List<String>
)

data class CreateInformation(
    val year: Int,
    val season: Int,
    val test_type: Int,
    val test_method: String,
    val strategy: String,
    val problems: List<String>
)