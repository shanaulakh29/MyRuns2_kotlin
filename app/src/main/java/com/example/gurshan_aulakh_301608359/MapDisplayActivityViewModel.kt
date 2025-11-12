package com.example.gurshan_aulakh_301608359

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapDisplayActivityViewModel: ViewModel(), ServiceConnection {
    var curLocation: MutableLiveData<Location> = MutableLiveData<Location>()
    val curSpeed: MutableLiveData<Double> = MutableLiveData<Double>()
    val duration: MutableLiveData<Double> = MutableLiveData<Double>()
    val distance:MutableLiveData<Double> = MutableLiveData<Double>()
    val avgSpeed:MutableLiveData<Double> = MutableLiveData<Double>()
    val calorie:MutableLiveData<Double> = MutableLiveData<Double>()

    val tempLocationList: MutableLiveData<ArrayList<LatLng>> = MutableLiveData(ArrayList())
    private var myMessageHander: MyMessageHandler
init{
    myMessageHander = MyMessageHandler(Looper.getMainLooper())
}
    override fun onServiceConnected(
        name: ComponentName?,
        iBinder: IBinder?
    ) {
        val binder = iBinder as TrackingService.MyBinder
        binder.setMessageHandler(myMessageHander)
    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }
    inner class MyMessageHandler(looper: Looper): Handler(looper) {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            duration.value = bundle.getDouble("totalTime")
            curSpeed.value = String.format("%.2f", bundle.getDouble("curSpeed")).toDouble()
            distance.value = String.format("%.2f", bundle.getDouble("distance")).toDouble()
            avgSpeed.value = String.format("%.2f", bundle.getDouble("avgSpeed")).toDouble()
            calorie.value = String.format("%.2f", bundle.getDouble("calorie")).toDouble()
            val lat = bundle.getDouble("latitude_key")
            val longt = bundle.getDouble("longitude_key")
            curLocation.value = Location("").apply{
                latitude=lat
                longitude=longt
            }
        }
    }
    fun addTempMarker(latLng: LatLng) {
        val currentList = tempLocationList.value ?: ArrayList()
        currentList.add(latLng)
        tempLocationList.value = currentList
    }
    fun clearTempMarkers() {
        tempLocationList.value = ArrayList()
    }

}