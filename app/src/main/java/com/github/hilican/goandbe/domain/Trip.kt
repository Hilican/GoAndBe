package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.domain.ItineraryItem

data class Trip(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val createdAt: Long = System.currentTimeMillis(), // Used for sorting
    val totalCost: Long = 0,
    val activities: List<ItineraryItem> = emptyList()
)
