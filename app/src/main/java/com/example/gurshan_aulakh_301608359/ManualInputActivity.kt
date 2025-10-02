package com.example.gurshan_aulakh_301608359

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


//Got idea about how to show dialogs from the lecture slides
class ManualInputActivity: AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var listview: ListView
    val items = listOf("Date","Time","Duration", "Distance", "Calories", "Heart Rate", "Comment")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manualinput)
        val activityType = intent.getStringExtra("activityType")

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

        saveButton.setOnClickListener {
            finish()
        }
        cancelButton.setOnClickListener {
            Toast.makeText(this,"Entry discarded" ,Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}