package com.example.gurshan_aulakh_301608359

import android.widget.Button
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabase
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabaseDao
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

class MapExerciseDisplayActivity : AppCompatActivity(),OnMapReadyCallback,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{
        private lateinit var mMap: GoogleMap
    private lateinit var database: ExerciseDatabase
    private lateinit var repository: ExerciseRepository
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var historyViewModel: HistoryViewModel



    private lateinit var calendar: Calendar
    private var duration: Double = 0.0
    private var curSpeed:Double = 0.0
    private var distance: Double = 0.0
    private var avgPace: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0
    private var heartRate: Double = 0.0
    private var comment: String = ""

    val activityTypeOptions = listOf("Running","Walking", "Standing", "Cycling","Hiking","Downhill Skiing","Cross-Country Skiing", "Snowboarding","Skating","Swimming","Mountain Biking","Wheelchair","Elliptical","Other")

    private lateinit var viewModelFactory: HistoryViewModelFactory

    private lateinit var avgSpeedValue: TextView
    private lateinit var curSpeedValue: TextView
    private lateinit var climbValue: TextView
    private lateinit var calorieValue: TextView
    private lateinit var distanceValue: TextView
    private lateinit var typeValue: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_exercise_display)

        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this,viewModelFactory).get(HistoryViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_summary) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initializeVariablesFromDatabase()

        val deleteButton  = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            val id = intent.getLongExtra("id",-1)
            historyViewModel.delete(id)
            finish()
        }
    }

    private fun initializeVariablesFromDatabase() {
        typeValue = findViewById<TextView>(R.id.typeValue)
        avgSpeedValue = findViewById(R.id.avgSpeedValue)
        curSpeedValue = findViewById(R.id.curSpeedValue)
        climbValue = findViewById(R.id.climbValue)
        calorieValue = findViewById(R.id.calorieValue)
        distanceValue = findViewById(R.id.distanceValue)


        typeValue.text = activityTypeOptions[intent.getIntExtra("activityType", 0)]
        avgSpeedValue.text = intent.getDoubleExtra("avgSpeed", 0.0).toString()
        curSpeedValue.text = intent.getDoubleExtra("avgPace", 0.0).toString()
        climbValue.text = intent.getDoubleExtra("climb", 0.0).toString()
        calorieValue.text = intent.getDoubleExtra("calorie", 0.0).toString()
        distanceValue.text = intent.getDoubleExtra("distance", 0.0).toString()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType= GoogleMap.MAP_TYPE_NORMAL
    }

    override fun onMapClick(p0: LatLng) {
        TODO("Not yet implemented")
    }

    override fun onMapLongClick(p0: LatLng) {
        TODO("Not yet implemented")
    }
}