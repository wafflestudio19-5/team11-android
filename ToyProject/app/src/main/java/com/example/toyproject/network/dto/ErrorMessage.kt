package com.example.toyproject.network.dto

import java.lang.StringBuilder

data class ErrorMessage(
    val error : String?,
    val detail : String?,
    val nickname : List<String>?,
    val password : List<String>?,
    val name : List<String>?,
    val user_id : List<String>?,
    val non_field_errors : List<String>?
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
        errorMessage?.name != null -> {
            builder.append("name : ")
            builder.append(errorMessage.name[0])
            return builder.toString()
        }
        errorMessage?.user_id != null -> {
            builder.append("ID : ")
            builder.append(errorMessage.user_id[0])
            return builder.toString()
        }
        errorMessage?.non_field_errors != null -> {
            errorMessage.non_field_errors.toString()
        }

        else -> {
            "Error"
        }
    }

}