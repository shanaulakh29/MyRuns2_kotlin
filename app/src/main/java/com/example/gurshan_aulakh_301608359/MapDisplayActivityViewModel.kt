package com.example.gurshan_aulakh_301608359

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapDisplayActivityViewModel: ViewModel(), ServiceConnection {
    val curSpeed: MutableLiveData<Double> = MutableLiveData<Double>()
    val distance:MutableLiveData<Double> = MutableLiveData<Double>()
    val avgSpeed:MutableLiveData<Double> = MutableLiveData<Double>()
    val calorie:MutableLiveData<Double> = MutableLiveData<Double>()
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


        }
    }
}