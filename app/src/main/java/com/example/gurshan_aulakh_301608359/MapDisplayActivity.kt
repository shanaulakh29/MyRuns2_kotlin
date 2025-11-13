package com.example.gurshan_aulakh_301608359

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Calendar

class MapDisplayActivity: AppCompatActivity() , OnMapReadyCallback{
    // NEW: Marker references for tracking
    private var startMarker: Marker? = null
    private var currentMarker: Marker? = null
    private var hasSetStartLocation = false
    private var polyline: Polyline?=null
    private var IS_HISTORY_MODE=true
    private var isMarkersAlreadyShownInHistoryTab=false
    private var IS_MAP_READY=false
    private var isStartLocationMarked=false
    private lateinit var serviceIntent: Intent
    private lateinit var mapDisplayActivityViewModel: MapDisplayActivityViewModel
    private var calendar: Calendar = Calendar.getInstance()
    private lateinit var locationList: ArrayList<LatLng>
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
    private var id:Long = -1
    private lateinit var tempLocationList:ArrayList<LatLng>

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

        id = intent.getLongExtra("id",-1)
        if(id.toInt()==-1){
            IS_HISTORY_MODE=false
        }
        locationList= ArrayList<LatLng>()
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
            mapDisplayActivityViewModel = ViewModelProvider(this)[MapDisplayActivityViewModel::class.java]
            tempLocationList = mapDisplayActivityViewModel.tempLocationList.value?: ArrayList()
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
        mapDisplayActivityViewModel.tempLocationList.observe(this) { list ->
            if(IS_MAP_READY){
                tempLocationList=list
                showTempLocationListMarkers()
            }
        }
        mapDisplayActivityViewModel.curLocation.observe(this){
                loc->
            updateCurrentLocationOnMap(loc)
        }

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
        mapDisplayActivityViewModel.duration.observe(this){
                time->
            duration = time
        }
    }
    private fun showTempLocationListMarkers() {
        if(tempLocationList.size==0){
            return
        }
        // Only set start marker once
        if (!hasSetStartLocation) {
            val startLatlng = tempLocationList[0]
            if(tempLocationList.size==1){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
            }
            startMarker = mMap.addMarker(
                MarkerOptions()
                    .position(startLatlng)
                    .title("Start Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            hasSetStartLocation = true
            println("START MARKER CREATED AT: $startLatlng")
        }
        if (tempLocationList.size > 1) {
            polyline?.remove()
            val currentLatlng = tempLocationList[tempLocationList.size - 1]

            // Remove old current marker
            currentMarker?.remove()

            // Add new current marker at latest position
            currentMarker = mMap.addMarker(
                MarkerOptions()
                    .position(currentLatlng)
                    .title("Current Location")
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, 16f))

            println("CURRENT MARKER UPDATED AT: $currentLatlng")
            println("TEMPLOCATION_LIST_SIZE IS ${tempLocationList.size}")

            // Draw polyline ONLY between start and current position
            val startLatlng = tempLocationList[0]
            polyline = mMap.addPolyline(PolylineOptions()
                .addAll(tempLocationList)
                .color(Color.BLUE)
                .width(5f))
        }

//        mMap.clear()
//        val startLatlng = tempLocationList[0]
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
//        mMap.addMarker(MarkerOptions().position(startLatlng).title("Start Location"))
//        println("TEMPLOCATION_LIST_SIZE IS "+tempLocationList.size)
//        if (tempLocationList.size > 1) {
//            for (i in 1 until tempLocationList.size) {
//                mMap.addMarker(MarkerOptions().position(tempLocationList[i]))
//                val polylineOptions = PolylineOptions()
//                    .add(startLatlng)
//                    .add(tempLocationList[i])
//                    .color(Color.BLUE).width(5f)
//                mMap.addPolyline(polylineOptions)
//            }
//        }
    }

    private fun updateCurrentLocationOnMap(loc: Location){
        val latlng = LatLng(loc.latitude,loc.longitude)
        mapDisplayActivityViewModel.addTempMarker(latlng)
    }

    private fun registerSaveCancelAndDeleteButtons() {
        saveButton.setOnClickListener {
            locationList = mapDisplayActivityViewModel.tempLocationList.value ?: ArrayList()
            exercise = ExerciseEntry(0L,inputTypeIndex, activityTypeIndex, calendar,duration, distance, avgPace, avgSpeed, calories, climb, heartRate, comment, locationList)
            historyViewModel.insert(exercise)
            applicationContext.unbindService(mapDisplayActivityViewModel)
            stopService( serviceIntent)
            mapDisplayActivityViewModel.clearTempMarkers()
            // UPDATED: Reset markers and flags
            startMarker = null
            currentMarker = null
            hasSetStartLocation = false
            isStartLocationMarked = false
            finish()
        }
        cancelButton.setOnClickListener {
            applicationContext.unbindService(mapDisplayActivityViewModel)
            stopService( serviceIntent)
            mapDisplayActivityViewModel.clearTempMarkers()
            finish()
        }
        deleteButton.setOnClickListener {
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
//        mMap.setOnMapClickListener(this)
//        mMap.setOnMapLongClickListener(this)
        IS_MAP_READY=true
        if(!IS_HISTORY_MODE){
            showTempLocationListMarkers()
        }

        if(!isMarkersAlreadyShownInHistoryTab && IS_HISTORY_MODE){
            showLocationListMarkers()
        }
    }

//    override fun onMapClick(p0: LatLng) {
//        if (IS_HISTORY_MODE) return
//    }

//    override fun onMapLongClick(latlng: LatLng) {
//        if (IS_HISTORY_MODE) return
//        mapDisplayActivityViewModel.addTempMarker(latlng)
//
//    }

    private fun initializeVariablesFromDatabase() {
        if(id>=0){
            historyViewModel.getExerciseEntry(id).observe(this){ entry->
                if(entry!=null){
                    locationList = entry.locationList
                    if(!isMarkersAlreadyShownInHistoryTab){
                        showLocationListMarkers()
                    }
                }else{
                    locationList= ArrayList<LatLng>()
                }
            }
        }
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

    private fun showLocationListMarkers() {
        if (locationList.isEmpty() || !IS_MAP_READY) return  // no markers to show
        println("LOCATIONLIST SIZE IN HISTORY MODE IS"+locationList.size)
        val startLatlng = locationList[0]
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
        mMap.addMarker(MarkerOptions().position(startLatlng).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        if (locationList.size > 1) {
            val endLatlng = locationList[locationList.size - 1]
            mMap.addMarker(MarkerOptions().position(endLatlng).title("End"))
            polyline = mMap.addPolyline(PolylineOptions()
                .addAll(locationList)
                .color(Color.BLUE)
                .width(5f))
            isMarkersAlreadyShownInHistoryTab=true
        }
    }


    private fun setAvgSpeedTextViewValue(speedParam:Double) {
        var unit="mph"
        var speed = speedParam
        if(unitPref=="0"){
            unit = "km/h"
            speed = speed*1.60934
        }

        avgSpeed = speed
        speed = String.format("%.2f", speed).toDouble()
        avgSpeedTextView.text = "${speed} ${unit}"
    }
    private fun setCurSpeedTextViewValue(speedParam:Double) {
        var unit="mph"
        var speed = speedParam
        if(unitPref=="0"){
            unit = "km/h"
            speed  = speed*1.60934
        }
        curSpeed = speed
        speed = String.format("%.2f", speed).toDouble()
        curSpeedTextView.text = "${speed} ${unit}"
    }
    private fun setDistanceTextViewValue(distParam:Double) {
        var unit="Miles"
        var dist=distParam
        if(unitPref=="0"){
            unit = "Kilometers"
            dist=distParam*1.60934
        }

        distance = dist
        dist = String.format("%.2f", dist).toDouble()
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

