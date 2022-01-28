package com.example.toyproject.network.dto.table

data class Lecture (
    val id : Int,
    val subject_name : String,
    val professor : String?,
    val subject_code : String,
    val year : Int,
    val season : String,
    val college : String?,
    val department : String?,
    val grade : Int,
    val level : String,
    val credit : Int,
    val category : String,
    val number : Int,
    val detail : String?,
    val language : String,
    val time : String?,
    val location : String?,
    val subject_professor : Int,
    val method : String,
    val url : String,
    val rate : Int,
    val quota : Int,
    val people : Int
)

data class CustomLecture (
    val id : Int,
    val lecture : Int?,
    val subject_professor: Int?,
    val nickname : String,
    val professor : String?,
    val time : String?,
    val location : String?,
    val memo : String?,
    val time_location : List<TimeLocationGet>?
)

data class TimeLocationGet(
    val time : String,
    val location : List<String>?
)
data class TimeLocationSend(
    val time : String,
    val location : String
)

// 사용자 추가 강의 서버에 추가할 때
data class UserMadeLectureAdd (
    val nickname : String,
    val professor : String?,
    val time_location : List<TimeLocationSend>?
)

data class EditCustomLecture(
    val nickname : String?,
    val memo : String?,
    val time_location : List<TimeLocationSend>?
)

data class ServerLectureList (
    val count : Int,
    val next : String?,
    val previous : String?,
    val results : List<Lecture>
)

data class AddCustomLectureById (
    val lecture : Int,
    val memo : String=""
)