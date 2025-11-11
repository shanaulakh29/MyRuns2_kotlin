package com.example.gurshan_aulakh_301608359

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabase
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabaseDao
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

class MapDisplayActivity: AppCompatActivity() , OnMapReadyCallback,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{
    private var IS_HISTORY_MODE=true
    private lateinit var serviceIntent: Intent
    private lateinit var mapDisplayActivityViewModel: MapDisplayActivityViewModel
    private var calendar: Calendar = Calendar.getInstance()
    private var duration: Double = 0.0
    private var curSpeed:Double = 0.0
    private var distance: Double = 0.0
    private var avgPace: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0
    private var heartRate: Double = 0.0
    private var comment: String = ""
    private lateinit var exercise: ExerciseEntry
    private val inputTypeOptions = listOf("Manual Entry", "GPS", "Automatic")
    val activityTypeOptions = listOf("Running","Walking", "Standing", "Cycling","Hiking","Downhill Skiing","Cross-Country Skiing", "Snowboarding","Skating","Swimming","Mountain Biking","Wheelchair","Elliptical","Other")
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var mMap: GoogleMap
    private lateinit var cancelButton: Button

    private lateinit var database: ExerciseDatabase
    private lateinit var repository: ExerciseRepository
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var viewModelFactory: HistoryViewModelFactory
    private var inputTypeIndex = 0
    private var activityTypeIndex=0

    private lateinit var avgSpeedTextView: TextView
    private lateinit var curSpeedTextView: TextView
    private lateinit var climbTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var unitPref: String? = "0"

    private val permissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false

            if (locationGranted && notificationGranted) {
                startService()
            } else {
                // Handle denied permissions
            }
        }
    private fun startService(){
        startService( serviceIntent)
        applicationContext.bindService(serviceIntent, mapDisplayActivityViewModel,Context.BIND_AUTO_CREATE )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapdisplay)
        val id = intent.getLongExtra("id",-1)
        if(id.toInt()==-1){
            IS_HISTORY_MODE=false
        }

        initializeButtons()

        inputTypeIndex = intent.getIntExtra("inputType",0)
        activityTypeIndex = intent.getIntExtra("activityType",0)
        val activityType = activityTypeOptions[activityTypeIndex]

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        unitPref = sharedPreferences.getString("unitPreference","0")

        initializeTextViews(activityType)
        initializeDatabaseViewModel()
        registerSaveCancelAndDeleteButtons()

        if(!IS_HISTORY_MODE){
            deleteButton.visibility = View.GONE
            serviceIntent = Intent(this, TrackingService::class.java)
            mapDisplayActivityViewModel = MapDisplayActivityViewModel()
            askPermissions()
            registerViewModelVariablesToObserve()

        }else{//User just want to see the data stored in the database
            saveButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
            initializeVariablesFromDatabase()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initializeButtons() {
        saveButton= findViewById<Button>(R.id.saveButton)
        cancelButton = findViewById<Button>(R.id.cancelButton)
        deleteButton = findViewById<Button>(R.id.deleteButton)
    }

    private fun registerViewModelVariablesToObserve() {
        mapDisplayActivityViewModel.distance.observe(this){
            dist->
            setDistanceTextViewValue(dist)
        }
        mapDisplayActivityViewModel.curSpeed.observe(this){
                speed->
            setCurSpeedTextViewValue(speed)
        }
        mapDisplayActivityViewModel.avgSpeed.observe(this){
                speed->
            setAvgSpeedTextViewValue(speed)
        }
        mapDisplayActivityViewModel.calorie.observe(this){
                calorie->
            calories = calorie
            calorieTextView.text = calorie.toString()
        }
    }

    private fun registerSaveCancelAndDeleteButtons() {

        saveButton.setOnClickListener {
            exercise = ExerciseEntry(0L,inputTypeIndex, activityTypeIndex, calendar,duration, distance, avgPace, avgSpeed, calories, climb, heartRate, comment)
            historyViewModel.insert(exercise)
            finish()
        }
        cancelButton.setOnClickListener {
            applicationContext.unbindService(mapDisplayActivityViewModel)
            stopService( serviceIntent)
            finish()
        }
        deleteButton.setOnClickListener {
            val id = intent.getLongExtra("id",-1)
            if(id>=0){
                historyViewModel.delete(id)
            }
            finish()
        }
    }
    private fun initializeTextViews(activityType:String){
        typeTextView = findViewById<TextView>(R.id.typeValue)
        avgSpeedTextView = findViewById(R.id.avgSpeedValue)
        curSpeedTextView = findViewById(R.id.curSpeedValue)
        climbTextView = findViewById(R.id.climbValue)
        calorieTextView = findViewById(R.id.calorieValue)
        distanceTextView = findViewById(R.id.distanceValue)

        typeTextView.text = activityType
        var unit="Miles"
        if(unitPref=="0"){
            unit="kilometers"
        }
        climb = 0.0
        climbTextView.text = "${climb} ${unit}"
    }

    fun initializeDatabaseViewModel(){
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this,viewModelFactory).get(HistoryViewModel::class.java)
    }
    override fun onMapReady(googeMap: GoogleMap) {
       mMap = googeMap
        mMap.mapType= GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)

    }

    override fun onMapClick(p0: LatLng) {

    }

    override fun onMapLongClick(p0: LatLng) {

    }

    private fun initializeVariablesFromDatabase() {
        typeTextView.text = activityTypeOptions[intent.getIntExtra("activityType", 0)]

        setAvgSpeedTextViewValue(intent.getDoubleExtra("avgSpeed", 0.0))
        setCurSpeedTextViewValue(intent.getDoubleExtra("curSpeed", 0.0))
        setDistanceTextViewValue(intent.getDoubleExtra("distance", 0.0))

        var unit="Miles"
        if(unitPref=="0"){
            unit="kilometers"
        }
        climbTextView.text = "${intent.getDoubleExtra("climb", 0.0)} ${unit}"
        calorieTextView.text = intent.getDoubleExtra("calorie", 0.0).toString()


    }

    private fun setAvgSpeedTextViewValue(speedParam:Double) {
        var unit="mph"
        if(unitPref=="0"){
            unit = "km/h"
        }
        var speed = speedParam
        avgSpeed = speedParam
        avgSpeedTextView.text = "${speed} ${unit}"
    }
    private fun setCurSpeedTextViewValue(speedParam:Double) {
        var unit="mph"
        if(unitPref=="0"){
            unit = "km/h"
        }
        var speed = speedParam
        curSpeed = speedParam
        curSpeedTextView.text = "${speed} ${unit}"
    }
    private fun setDistanceTextViewValue(distParam:Double) {
        var unit="Miles"
        if(unitPref=="0"){
            unit = "Kilometers"
        }
        var dist=distParam
        distance = distParam
        distanceTextView.text = "${dist} ${unit}"
    }



    fun askPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All permissions already granted
            startService()
        }
    }
    override fun onResume(){
        super.onResume()
        unitPref = sharedPreferences.getString("unitPreference","0")
    }
}