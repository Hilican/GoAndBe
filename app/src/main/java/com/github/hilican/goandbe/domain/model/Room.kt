package com.github.hilican.goandbe.domain.model

data class Room(
    val id: String,
    val roomType: String,
    val price: Float,
    val images: List<String>
)
