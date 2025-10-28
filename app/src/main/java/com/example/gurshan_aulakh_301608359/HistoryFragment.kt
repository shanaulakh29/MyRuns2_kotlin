package com.example.gurshan_aulakh_301608359

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabase
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabaseDao
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository


class HistoryFragment : Fragment() {
    private lateinit var database: ExerciseDatabase
    private lateinit var repository: ExerciseRepository
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var arrayAdapter: HistoryPageAdapter
    private lateinit var arrayList: ArrayList<ExerciseEntry>
    private lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_history, container, false)

        database = ExerciseDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        val viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(requireActivity(),viewModelFactory).get(HistoryViewModel::class.java)

        listView = view.findViewById<ListView>(R.id.listView)
        arrayList= ArrayList()
        arrayAdapter = HistoryPageAdapter(requireActivity(),arrayList)
        listView.adapter = arrayAdapter
        historyViewModel.allExercisesLiveData.observe(requireActivity(), Observer { list ->
            arrayList.clear()
            arrayList.addAll(list)
            arrayAdapter.notifyDataSetChanged()
        })
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = arrayList[position]
            val intent = Intent(requireActivity(), ExerciseDetailActivity::class.java)
            intent.putExtra("inputType", selectedItem.inputType)
            intent.putExtra("activityType", selectedItem.activityType)
            intent.putExtra("dateTime",selectedItem.dateTime.timeInMillis)
            intent.putExtra("duration", selectedItem.duration)
            intent.putExtra("distance", selectedItem.distance)
            intent.putExtra("avgPace", selectedItem.avgPace)
            intent.putExtra("avgSpeed", selectedItem.avgSpeed)
            intent.putExtra("calorie", selectedItem.calorie)
            intent.putExtra("climb", selectedItem.climb)
            intent.putExtra("heartRate", selectedItem.heartRate)
            intent.putExtra("comment", selectedItem.comment)
            intent.putExtra("id", selectedItem.id)
            startActivity(intent)
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        arrayAdapter.notifyDataSetChanged()
    }
}