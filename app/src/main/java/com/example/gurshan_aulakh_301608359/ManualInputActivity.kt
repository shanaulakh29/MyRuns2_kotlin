package com.example.gurshan_aulakh_301608359

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabase
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabaseDao
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar


//Got idea about how to show dialogs from the lecture slides
class ManualInputActivity: AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var listview: ListView
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var repository: ExerciseRepository
    private var calendar: Calendar = Calendar.getInstance()

    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var avgPace: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0
    private var heartRate: Double = 0.0
    private var comment: String = ""
    private lateinit var exercise: ExerciseEntry
    private lateinit var viewModelFactory: HistoryViewModelFactory
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    val items = listOf("Date","Time","Duration", "Distance", "Calories", "Heart Rate", "Comment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manualinput)
        val activityType = intent.getIntExtra("activityType",0)
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this,viewModelFactory).get(HistoryViewModel::class.java)
        saveButton = findViewById<Button>(R.id.saveButton)
        cancelButton = findViewById<Button>(R.id.cancelButton)
        //Got idea from lecture notes about how to use listview and how to set up the listview adapter
        listview = findViewById<ListView>(R.id.listview)
        val adapter = ArrayAdapter<String>(this, R.layout.list_items, R.id.textview, items )
        listview.adapter = adapter

        listview.setOnItemClickListener{
            parent,view,position, id ->
            val clickedItem = items[position]
            val myDialog = MyRunsDialogFragment()
            val bundle = Bundle()
            if(clickedItem==items[0]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,1)
            }else if(clickedItem==items[1]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,2)
            }else if(clickedItem==items[2]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,3)
            }else if(clickedItem==items[3]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,4)
            }else if(clickedItem==items[4]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,5)
            }else if(clickedItem==items[5]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,6)
            }else if(clickedItem==items[6]){
                bundle.putInt(MyRunsDialogFragment.DIALOG_KEY,7)
            }

            myDialog.arguments=bundle
            myDialog.show(supportFragmentManager, "my dialog")
        }
        supportFragmentManager.setFragmentResultListener("dateSelected", this) { _, bundle ->
            val year = bundle.getInt("year")
            val month = bundle.getInt("month")
            val day = bundle.getInt("day")
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

        }

        supportFragmentManager.setFragmentResultListener("timeSelected", this) { _, bundle ->
            val hour = bundle.getInt("hour")
            val minute = bundle.getInt("minute")
            calendar.set(Calendar.HOUR_OF_DAY,hour)
            calendar.set(Calendar.MINUTE,minute)
        }

        supportFragmentManager.setFragmentResultListener("durationSelected", this) { _, bundle ->
            duration = bundle.getString("duration")?.toDoubleOrNull() ?: 0.0
        }

        supportFragmentManager.setFragmentResultListener("distanceSelected", this){_, bundle ->
            distance = bundle.getString("distance")?.toDoubleOrNull() ?: 0.0
            if(distance>0.0){
                val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val unitPref = sharedPreferences.getString("unitPreference","0")
                //storing the distance in miles in database
                if(unitPref=="0"){//user is entering in kilometres
                    distance = distance/1.60934
                }
            }
        }

        supportFragmentManager.setFragmentResultListener("caloriesSelected", this) { _, bundle ->
            calories = bundle.getString("calories")?.toDoubleOrNull() ?: 0.0
        }

        supportFragmentManager.setFragmentResultListener("heartRateSelected", this) { _, bundle ->
            heartRate = bundle.getString("heartRate")?.toDoubleOrNull() ?: 0.0
        }

        supportFragmentManager.setFragmentResultListener("commentSelected", this) { _, bundle ->
            comment = bundle.getString("comment").toString()
        }

        saveButton.setOnClickListener {
            val locationList = ArrayList<LatLng>()
            exercise = ExerciseEntry(0L,0, activityType, calendar,duration, distance, avgPace, avgSpeed, calories, climb, heartRate, comment,locationList)
            historyViewModel.insert(exercise)
            finish()
        }
        cancelButton.setOnClickListener {
            Toast.makeText(this,"Entry discarded" ,Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}