package com.example.gurshan_aulakh_301608359

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat


class TrackingService : Service(), LocationListener {
    private var startTime: Long = 0L
    private var avgSpeed = 0.0
    private var curSpeed = 0.0
    private var totalCalorie = 0.0
    private var totalDistance:Double=0.0
    private var totalTimeInSecs:Double=0.0
    private var prevLocation:Location?=null
    private val NOTIFICATION_ID = 111
    private lateinit var locationManager: LocationManager
    private var CHANNEL_ID="Notification Channel"
    private var messageHandler: Handler?=null
    private lateinit var notificationManager: NotificationManager
    private lateinit var myBinder: MyBinder

    override fun onCreate() {
        super.onCreate()
        myBinder = MyBinder()
        showNotification()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location!=null){
                onLocationChanged(location)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        } else {
            println("Location permission missing")
        }
    }
    override fun onLocationChanged(location: Location) {
        if (prevLocation != null) {
            // Speed in MPH
            curSpeed = location.speed.toDouble() * 2.23694

            // Time in seconds since start
            totalTimeInSecs = (System.currentTimeMillis() - startTime) / 1000.0

            // Total distance in miles
            var deltaDistance = prevLocation!!.distanceTo(location).toDouble() / 1609.344 // meters to miles
            totalDistance += deltaDistance

            // Average speed in MPH
            val totalTimeHours = totalTimeInSecs / 3600.0
            avgSpeed = if (totalTimeHours > 0) totalDistance / totalTimeHours else 0.0

            // Calories burned
            totalCalorie += deltaDistance * 80

            prevLocation = location
        } else {
            startTime = System.currentTimeMillis()
            prevLocation = location
        }

        // Only format for display, not for calculations
        if (messageHandler != null) {
            val bundle = Bundle()
            bundle.putDouble("totalTime", totalTimeInSecs)
            bundle.putDouble("curSpeed", curSpeed)
            bundle.putDouble("avgSpeed", avgSpeed)
            bundle.putDouble("distance", totalDistance)
            bundle.putDouble("calorie", totalCalorie)
            bundle.putDouble("longitude_key", location.longitude)
            bundle.putDouble("latitude_key", location.latitude)

            val msg = messageHandler!!.obtainMessage()
            msg.data = bundle
            messageHandler!!.sendMessage(msg)

            // Display nicely
            println(
                "totalTimeInSecs = ${"%.2f".format(totalTimeInSecs)}, " +
                        "curSpeed = ${"%.2f".format(curSpeed)}, " +
                        "avgSpeed = ${"%.2f".format(avgSpeed)}, " +
                        "totalDistance = ${"%.2f".format(totalDistance)}, " +
                        "totalCalorie = ${"%.2f".format(totalCalorie)}, " +
                        "longitude = ${location.longitude}, latitude = ${location.latitude}"
            )
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("debug: Service onStartCommand() called everytime startService() is called; startId: $startId flags: $flags")
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder {
        println("Tracking Service onBind Called")
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("Tracking Service onUnBind Called")
        messageHandler=null
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NOTIFICATION_ID)
    }
    private fun showNotification() {
        val intent = Intent(this, MapDisplayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,0,intent,PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.notification_icon)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setContentTitle("Tracking Service Started😀")
        notificationBuilder.setContentText("")
        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID,notification)
    }



    inner class MyBinder: Binder(){
        fun setMessageHandler(messageHandler: Handler){
            this@TrackingService.messageHandler = messageHandler
        }
    }

}