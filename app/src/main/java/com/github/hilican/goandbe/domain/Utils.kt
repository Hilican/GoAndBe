package com.github.hilican.goandbe.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertMillisToString(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun convertMillisToStringForApi(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

fun convertStringToMillisFromApi(dateString: String): Long {
    return try {
        // 🌟 CORRECCIÓN: Cambiado de "dd-MM-yyyy" a "yyyy-MM-dd"
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        formatter.parse(dateString)?.time ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

fun calculateTotalCost(startDate: String, endDate: String, pricePerNight: Float): Float {
    val startMillis = convertStringToMillisFromApi(startDate)
    val endMillis = convertStringToMillisFromApi(endDate)

    val totalNights = ((endMillis - startMillis) / (24L * 60 * 60 * 1000)).toInt().coerceAtLeast(1)

    return pricePerNight * totalNights
}