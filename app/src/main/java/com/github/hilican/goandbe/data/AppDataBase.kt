package com.github.hilican.goandbe.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [User::class, Trip::class, ItineraryItem::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
}