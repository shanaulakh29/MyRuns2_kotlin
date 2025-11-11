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
            curSpeed.value = bundle.getDouble("curSpeed")
            distance.value = bundle.getDouble("distance")
            avgSpeed.value = bundle.getDouble("avgSpeed")
            calorie.value = bundle.getDouble("calorie")
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