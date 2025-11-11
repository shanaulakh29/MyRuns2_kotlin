package com.example.gurshan_aulakh_301608359

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

//myruns2 instructions mention to use spinner so i understood how to sue spinner from the kotlin official site
//https://developer.android.com/develop/ui/views/components/spinner
class StartFragment : Fragment() {
    private lateinit var inputTypeSpinner: Spinner
    private lateinit var startButton: Button
    private val inputTypeOptions = listOf("Manual Entry", "GPS", "Automatic")
    val activityTypeOptions = listOf("Running","Walking", "Standing", "Cycling","Hiking","Downhill Skiing","Cross-Country Skiing", "Snowboarding","Skating","Swimming","Mountain Biking","Wheelchair","Elliptical","Other")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_start, container, false)
        inputTypeSpinner = view.findViewById<Spinner>(R.id.inputTypeSpinner)

        val inputTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, inputTypeOptions)
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputTypeSpinner.adapter = inputTypeAdapter

        val activityTypeSpinner = view.findViewById<Spinner>(R.id.activityTypeSpinner)

        val activityTypeAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, activityTypeOptions )
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityTypeSpinner.adapter=activityTypeAdapter

        startButton = view.findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            val selectedInputTypeIndex = inputTypeSpinner.selectedItemPosition
            val selectedInputType = inputTypeOptions[selectedInputTypeIndex]
            val selectedActivityType = activityTypeSpinner.selectedItemPosition
            if(selectedInputType==inputTypeOptions[0]){
                val intent = Intent(requireContext(),ManualInputActivity::class.java)
                intent.putExtra("activityType",selectedActivityType)
                startActivity(intent)
            }else{
                val intent = Intent(requireContext(),MapDisplayActivity::class.java)
                intent.putExtra("activityType",selectedActivityType)
                intent.putExtra("inputType",selectedInputTypeIndex)
                startActivity(intent)
            }
        }
        return view
    }
}