package com.eslam.csp1401_test.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [EventEntity::class], version = 1,exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class EventDataBase: RoomDatabase() {

    abstract fun getEventsDao():EventsDao
}