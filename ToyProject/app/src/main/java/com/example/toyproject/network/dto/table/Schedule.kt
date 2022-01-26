package com.example.toyproject.network.dto.table

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Schedule (
    val id : Int,
    val name : String,
    val year : Int,
    val season : Int,
    val last_visit : String,
    val is_default : Boolean
)
data class Semester (
    val year : Int,
    val season : Int,
    val schedules : List<Schedule>
)

// @GET schedule_list 할 때 응답
data class ScheduleListGet (
    val count : Int,
    val next : String?,
    val previous : String?,
    val results : List<Schedule>
)

// 기본 시간표 받아오기
data class DefaultSchedule (
    val id : Int,
    val name : String,
    val year : Int,
    val season : Int,
    val last_visit : String,
    val is_default : Boolean
)

// 기본 시간표의 custom lecture 목록 받아오기
data class DefaultScheduleLectureList(
    val custom_lectures : List<CustomLecture>
)

data class ScheduleCreate(
    val name : String,
    val year : Int,
    val season : Int
)
