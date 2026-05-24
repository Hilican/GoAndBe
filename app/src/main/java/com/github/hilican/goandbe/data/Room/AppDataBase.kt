package com.github.hilican.goandbe.data.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [
    UserRoom::class,
    Trip::class,
    ItineraryItem::class,
    ReservationRoom::class],
    version = 1
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
}