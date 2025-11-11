package com.example.gurshan_aulakh_301608359

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository
//Got idea from lecture notes about how to create database and how to connect the View to the database using repository and viewModel
class HistoryViewModel(private val exerciseRepository: ExerciseRepository): ViewModel() {
val allExercisesLiveData: LiveData<List<ExerciseEntry>> = exerciseRepository.allExercises.asLiveData()
    fun insert(exerciseEntry: ExerciseEntry){
        exerciseRepository.insert(exerciseEntry)
    }
    fun delete(id: Long){
        exerciseRepository.delete(id)
    }
}
class HistoryViewModelFactory (private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java))
            return HistoryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}