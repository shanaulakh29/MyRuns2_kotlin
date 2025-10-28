package com.example.gurshan_aulakh_301608359.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseRepository(private val exerciseDatabaseDao: ExerciseDatabaseDao) {

    val allExercises: Flow<List<ExerciseEntry>> = exerciseDatabaseDao.getAllExercises()

     fun insert(exercise: ExerciseEntry){
         CoroutineScope(IO).launch{
             exerciseDatabaseDao.insertExercise(exercise)
         }
     }
    fun delete(id:Long){
        CoroutineScope(IO).launch{
            exerciseDatabaseDao.deleteExercise(id)
        }
    }
}