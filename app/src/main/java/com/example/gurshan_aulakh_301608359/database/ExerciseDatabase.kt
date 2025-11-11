package com.example.gurshan_aulakh_301608359.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
//Got idea from lecture notes about how to create database and how to connect the View to the database using repository and viewModel
@Database(entities = [ExerciseEntry::class],version=2)
@TypeConverters(TypeConverter::class)
abstract class ExerciseDatabase: RoomDatabase() {
    abstract val exerciseDatabaseDao: ExerciseDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: ExerciseDatabase?=null

        fun getInstance(context: Context):ExerciseDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext, ExerciseDatabase::class.java, "ExerciseDatabase").fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}