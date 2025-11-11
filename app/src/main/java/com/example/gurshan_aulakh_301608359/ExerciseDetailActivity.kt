package com.example.gurshan_aulakh_301608359

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabase
import com.example.gurshan_aulakh_301608359.database.ExerciseDatabaseDao
import com.example.gurshan_aulakh_301608359.database.ExerciseRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.round

class ExerciseDetailActivity : AppCompatActivity() {
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var repository: ExerciseRepository
    private lateinit var inputTypeEditText: EditText
    private lateinit var activityTypeEditText: EditText
    private lateinit var dateAndTimeEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var heartRateEditText: EditText
    val activityTypeOptions = listOf("Running","Walking", "Standing", "Cycling","Hiking","Downhill Skiing","Cross-Country Skiing", "Snowboarding","Skating","Swimming","Mountain Biking","Wheelchair","Elliptical","Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise_detail)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        val viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)

        inputTypeEditText = findViewById(R.id.inputTypeEditText)
        activityTypeEditText = findViewById(R.id.activityTypeEditText)
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText)
        durationEditText = findViewById(R.id.durationEditText)
        distanceEditText = findViewById(R.id.distanceEditText)
        caloriesEditText = findViewById(R.id.caloriesEditText)
        heartRateEditText = findViewById(R.id.heartRateEditText)

        setInputType()
        setActivityType()
        setDateTime()

        setDistance()
        setDuration(durationEditText)


        val caloriesInt = intent.getDoubleExtra("calorie", 0.0).toInt()
        caloriesEditText.setText("$caloriesInt cals")

        val heartRateInt = intent.getDoubleExtra("heartRate", 0.0).toInt()
        heartRateEditText.setText("$heartRateInt bpm")

        deleteButton.setOnClickListener {
           historyViewModel.delete(intent.getLongExtra("id",-1))
            finish()
        }
    }
    fun setDistance(){
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPrefs = sharedPreferences.getString("unitPreference","0")

        var distance = intent.getDoubleExtra("distance", 0.0)
        var distanceUnit = "Miles"
        if(unitPrefs=="0"){
            distanceUnit = "Kilometres"
            distance = distance*1.60934
        }
//        distance = String.format("%.2f", distance).toDouble()
        distance = round(distance * 100) / 100
        if(distance==0.0){
            distanceEditText.setText("0 $distanceUnit")
            distanceEditText.keyListener = null
            return
        }
        distanceEditText.setText("$distance $distanceUnit")
        distanceEditText.keyListener = null
    }
    fun setDateTime(){
        val dateTimeMillis= intent.getLongExtra("dateTime",System.currentTimeMillis())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeMillis
        val dateFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("America/Vancouver")
        dateAndTimeEditText.setText(dateFormat.format(calendar.time))
    }
    fun setDuration(durationEditText: TextView){
        var duration = intent.getDoubleExtra("duration", 0.0)
        var durationText="0 secs"
        if(duration!=0.0){
            val minutes = duration.toInt()
            val seconds  = ((duration-minutes)*60).toInt()
            durationText = "$minutes mins $seconds secs"
        }

        durationEditText.text = durationText
    }

    fun setActivityType(){
        var activityTypeIndex = intent.getIntExtra("activityType",-1)
        if(activityTypeIndex>=0){
            activityTypeEditText.setText(activityTypeOptions.get(activityTypeIndex))
        }

    }
    fun setInputType(){
        var inputTypeIndex = intent.getIntExtra("inputType",-1)
        if(inputTypeIndex==0){
            inputTypeEditText.setText("Manual Entry")
        }else if(inputTypeIndex==1){
            inputTypeEditText.setText("GPS")
        }else if(inputTypeIndex==2){
            inputTypeEditText.setText("Automatic")
        }
    }
}
