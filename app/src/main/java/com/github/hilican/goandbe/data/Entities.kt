package com.github.hilican.goandbe.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class Testing(
    @PrimaryKey val userId: String,
    val email: String,
    val username: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
)