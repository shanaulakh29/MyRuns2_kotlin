package com.example.gurshan_aulakh_301608359

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class MyRunsDialogFragment : DialogFragment(), DialogInterface.OnClickListener {


    private lateinit var listView: ListView
    private val items=listOf<String>("Open Camera","Select from Gallery")
    companion object{
        const val DIALOG_KEY = "dialog"
        const val DATE_DIALOG= 1
        const val TIME_DIALOG= 2
        const val DURATION_DIALOG= 3
        const val DISTANCE_DIALOG= 4
        const val CALORIES_DIALOG= 5
        const val HEART_RATE_DIALOG= 6
        const val COMMENT_DIALOG= 7
        const val CAMERA_OR_FROM_GALLERY=8
    }

    //Understood from the live lecture demo about how to use AlertDialog.Builder to set up the dialog object
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        if(dialogId==DATE_DIALOG){
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            return DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Handle the selected date here
                },
                year, month, day
            )
        }else if(dialogId == TIME_DIALOG){
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)

            return TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    // Handle the selected time here
                },
                hour, minute, true // last parameter = 24-hour format
            )
        }else if(dialogId==DURATION_DIALOG){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.duration_dialog, null)
            builder.setView(view)
            builder.setTitle("Duration")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
        }else if(dialogId==DISTANCE_DIALOG){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.distance_dialog, null)
            builder.setView(view)
            builder.setTitle("Distance")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
        }else if(dialogId==CALORIES_DIALOG){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.calories_dialog, null)
            builder.setView(view)
            builder.setTitle("Calories")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
        }else if(dialogId==HEART_RATE_DIALOG){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.heartrate_dialog, null)
            builder.setView(view)
            builder.setTitle("Heart Rate")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
        }else if(dialogId==COMMENT_DIALOG){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.comments_dialog, null)
            val editText = view.findViewById<EditText>(R.id.commentEditText)
            editText.hint = "How did it go? Notes here"
            builder.setView(view)
            builder.setTitle("Comment")
            builder.setPositiveButton("ok", this)
            builder.setNegativeButton("cancel", this)
        }else if(dialogId==CAMERA_OR_FROM_GALLERY){
            val view: View = requireActivity().layoutInflater.inflate(R.layout.camera_or_from_gallery_dialog, null)
            listView = view.findViewById<ListView>(R.id.listView)
            val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,items)
            listView.adapter=adapter
            builder.setView(view)
            builder.setTitle("Pick Profile Picture")

            listView.setOnItemClickListener{
                    parent,view,position, id ->
                val result = Bundle()
                result.putInt("choice", position)  // 0 = camera, 1 = gallery
                parentFragmentManager.setFragmentResult("selectedChoice", result)
                dismiss()
            }
        }
        return builder.create()
    }
    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}