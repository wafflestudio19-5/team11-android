package com.example.toyproject.network.dto.table

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Schedule (
    val id : Int,
    val name : String,
    val year : Int,
    val season : Int,
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
    val result : List<Schedule>
)
