package com.example.gurshan_aulakh_301608359.database

import androidx.room.TypeConverter
import java.util.Calendar

class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value:Long): Calendar {
        return value.let{
            Calendar.getInstance().apply {
                timeInMillis = it
            }
        }
    }
    @TypeConverter
    fun calendarToTimeStamp(calendar: Calendar):Long{
        return calendar.timeInMillis
    }
}