package com.example.gurshan_aulakh_301608359

import android.content.Context
import android.content.SharedPreferences
import android.icu.util.Calendar
import androidx.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gurshan_aulakh_301608359.database.ExerciseEntry
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.round

class HistoryPageAdapter(private val context: Context, private var exerciseList: List<ExerciseEntry>):  BaseAdapter(){
    val activityTypeOptions = listOf("Running","Walking", "Standing", "Cycling","Hiking","Downhill Skiing","Cross-Country Skiing", "Snowboarding","Skating","Swimming","Mountain Biking","Wheelchair","Elliptical","Other")
    override fun getCount(): Int {
        return exerciseList.size
    }

    override fun getItem(position: Int): Any? {
        return exerciseList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view: View = View.inflate(context, R.layout.history_layout_adapter,null)

        val inputTypeTextView= view.findViewById<TextView>(R.id.inputType)
        setInputTypeTextView(inputTypeTextView, position)

        val activityTypeTextView = view.findViewById<TextView>(R.id.activityType)
        setActivityTypeTextView(activityTypeTextView, position)

        val dateTimeTextView = view.findViewById<TextView>(R.id.dateTime)
        setDateTime(position,dateTimeTextView)


        val distanceTextView = view.findViewById<TextView>(R.id.distance)
        setDistance(distanceTextView, position)

        val durationTextView = view.findViewById<TextView>(R.id.duration)
        setDuration(durationTextView, position)

        return view
    }
    fun setDateTime(position:Int, dateTimeTextView: TextView){
        val timeInMillis = exerciseList.get(position).dateTime.timeInMillis
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("America/Vancouver")
        dateTimeTextView.setText(dateFormat.format(calendar.time))
    }
    fun setDuration(durationTextView: TextView, position:Int){
        var duration = exerciseList.get(position).duration
        var durationText="0 secs"
        if(duration!=0.0){
            val minutes = duration.toInt() / 60
            val seconds = (duration.toInt() % 60)
            durationText = "$minutes mins $seconds secs"
        }
        durationTextView.text = durationText
    }
    fun setDistance(distanceTextView: TextView, position:Int){
        var distance  = exerciseList.get(position).distance

        val sharedPrfs = PreferenceManager.getDefaultSharedPreferences(context)
        val unitPrefs =  sharedPrfs.getString("unitPreference","0")
        var distanceUnit = "Miles"
        if(unitPrefs=="0"){
            distanceUnit = "Kilometres"
            distance = distance*1.60934
        }
//        distance = String.format("%.2f", distance).toDouble()
        distance = round(distance * 100) / 100
        if(distance==0.0){
            distanceTextView.setText("0 $distanceUnit")
            return
        }
        distanceTextView.text = "$distance $distanceUnit,"
    }
    fun setActivityTypeTextView(activityTypeTextView:TextView,position:Int){
        val activityTypeIndex = exerciseList.get(position).activityType
        if(activityTypeIndex>=0){
            activityTypeTextView.text = activityTypeOptions.get(activityTypeIndex)
        }
    }
    fun setInputTypeTextView(inputTypeTextView:TextView, position:Int){
        val inputTypeIndex = exerciseList.get(position).inputType
        if(inputTypeIndex==0){
            inputTypeTextView.text = "Manual Entity:"
        }else if(inputTypeIndex==1){
            inputTypeTextView.text = "GPS:"
        }else if(inputTypeIndex==2){
            inputTypeTextView.text = "Automatic:"
        }
    }
}