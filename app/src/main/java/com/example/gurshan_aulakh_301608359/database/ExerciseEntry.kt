package com.example.gurshan_aulakh_301608359.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "ExerciseEntry_table")
class ExerciseEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "inputType")
    var inputType: Int,

    @ColumnInfo(name = "activityType")
    var activityType: Int,

    @ColumnInfo(name = "dateTime")
    var dateTime: Calendar,

    @ColumnInfo(name = "duration")
    var duration: Double,

    @ColumnInfo(name = "distance")
    var distance: Double,

    @ColumnInfo(name = "avgPace")
    var avgPace: Double,

    @ColumnInfo(name = "avgSpeed")
    var avgSpeed: Double,

    @ColumnInfo(name = "calorie")
    var calorie: Double,

    @ColumnInfo(name = "climb")
    var climb: Double,

    @ColumnInfo(name = "heartRate")
    var heartRate: Double,

    @ColumnInfo(name = "comment")
    var comment: String,


)