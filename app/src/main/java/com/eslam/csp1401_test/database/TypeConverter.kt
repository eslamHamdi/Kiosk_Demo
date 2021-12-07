package com.eslam.csp1401_test.database

import androidx.room.TypeConverter
import com.eslam.csp1401_test.AttendeesItem
import com.google.gson.Gson


class TypeConverter {


   @TypeConverter
    fun listToJson(value: List<Attendee>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Attendee>::class.java).toList()
}