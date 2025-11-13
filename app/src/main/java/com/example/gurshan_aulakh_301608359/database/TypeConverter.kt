package com.example.gurshan_aulakh_301608359.database

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Calendar

class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value:Long): Calendar {
        return value.let{
            Calendar.getInstance().apply {
                timeInMillis = it
            }
        }
    }
    @TypeConverter
    fun calendarToTimeStamp(calendar: Calendar):Long{
        return calendar.timeInMillis
    }

//    @TypeConverter
//    fun fromLatLngList(list: ArrayList<LatLng>?):String{
//        if (list == null) return "[]"
//        val jsonArray = JSONArray()
//        for (latLng in list) {
//            val jsonObj = JSONArray()
//            jsonObj.put(latLng.latitude)
//            jsonObj.put(latLng.longitude)
//            jsonArray.put(jsonObj)
//        }
//        return jsonArray.toString()
//    }
//
//    @TypeConverter
//    fun toLatLngList(data: String?): ArrayList<LatLng> {
//        val list = ArrayList<LatLng>()
//        if (data.isNullOrEmpty()) return list
//        val jsonArray = JSONArray(data)
//        for (i in 0 until jsonArray.length()) {
//            val latLngArray = jsonArray.getJSONArray(i)
//            val lat = latLngArray.getDouble(0)
//            val lng = latLngArray.getDouble(1)
//            list.add(LatLng(lat, lng))
//        }
//        return list
//    }
@TypeConverter
fun fromLatLngList(locationList: ArrayList<LatLng>): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)

    for (latLng in locationList) {
        objectOutputStream.writeDouble(latLng.latitude)
        objectOutputStream.writeDouble(latLng.longitude)
    }

    objectOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}

    // The objectInputStream automatically convert raw bytes from byteArrayInputStream into doubles
    @TypeConverter
    fun toLatLngList(byteArray: ByteArray): ArrayList<LatLng> {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)

        val locationList = ArrayList<LatLng>()

        try {
            while (true) {
                val latitude = objectInputStream.readDouble()
                val longitude = objectInputStream.readDouble()
                locationList.add(LatLng(latitude, longitude))
            }
        } catch (_: Exception) {

        }

        objectInputStream.close()
        return locationList
    }

}