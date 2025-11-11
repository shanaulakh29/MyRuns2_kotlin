package com.example.gurshan_aulakh_301608359.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
//Got idea from lecture notes about how to create database and how to connect the View to the database using repository and viewModel
@Dao
interface ExerciseDatabaseDao {

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntry)

    @Query("Delete from exerciseentry_table where id = :id")
    suspend fun deleteExercise(id:Long)

    @Query("Select * from exerciseentry_table")
    fun getAllExercises(): Flow<List<ExerciseEntry>>
}
