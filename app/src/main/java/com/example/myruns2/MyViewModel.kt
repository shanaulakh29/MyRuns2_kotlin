package com.example.myruns2
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//understood the working of ViewModel and MutableLiveData from lecture
class MyViewModel: ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap?>()
    var profilePhotoUri: Uri?=null
}