package com.github.hilican.goandbe.viewmodel.utils

import org.json.JSONObject
import retrofit2.HttpException

object ErrorUtils {
    fun extractErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            json.optString("detail", "Unknown error")
        } catch (ex: Exception) {
            "Unknown error"
        }
    }
}