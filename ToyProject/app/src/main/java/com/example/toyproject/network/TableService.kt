package com.example.toyproject.network

import com.example.toyproject.network.dto.table.ScheduleListGet
import retrofit2.http.GET

interface TableService {

    @GET("/api/v1/schedule/")
    suspend fun getScheduleList() : ScheduleListGet
}