package com.example.toyproject.network.dto

import java.lang.StringBuilder

data class ErrorMessage(
    val error : String?,
    val detail : String?,
    val nickname : List<String>?,
    val password : List<String>?
)

fun parsing(errorMessage: ErrorMessage?) : String {
    val builder = StringBuilder()
    return when {
        errorMessage?.detail != null -> {
            errorMessage.detail.toString()
        }
        errorMessage?.nickname != null -> {
            builder.append("nickname : ")
            builder.append(errorMessage.nickname[0])
            return builder.toString()
        }
        errorMessage?.password != null -> {
            builder.append("password : ")
            builder.append(errorMessage.password[0])
            return builder.toString()
        }
        else -> {
            "Error"
        }
    }

}