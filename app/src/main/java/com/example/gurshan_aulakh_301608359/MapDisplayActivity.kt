package com.example.gurshan_aulakh_301608359

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MapDisplayActivity: AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapdisplay)
        val activityType = intent.getStringExtra("activityType")

        saveButton= findViewById<Button>(R.id.saveButton)
        cancelButton = findViewById<Button>(R.id.cancelButton)

        saveButton.setOnClickListener {
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }
}