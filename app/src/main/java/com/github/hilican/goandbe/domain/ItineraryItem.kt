package com.github.hilican.goandbe.domain

import java.util.Date

data class ItineraryItem(
    val id: Int,
    val description: String,
    val activityTime: Date,
    val costEstimate: Double,
)