package com.example.toyproject.network

import com.example.toyproject.network.dto.Board
import retrofit2.http.GET

interface BoardService {

    @GET("/api/v1/board/")
    suspend fun getBoardList() : List<Board>

}