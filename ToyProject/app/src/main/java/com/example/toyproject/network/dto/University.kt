package com.example.toyproject.network.dto

data class GetUniversity(
    val university_list : List<University>
)

data class University (
    val id : Int,
    val name : String
)

