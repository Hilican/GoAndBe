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