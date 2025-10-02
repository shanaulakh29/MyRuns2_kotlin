package com.example.gurshan_aulakh_301608359

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.io.File

class UserProfileActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioFemale: RadioButton
    private lateinit var radioMale: RadioButton
    private lateinit var editClass: EditText
    private lateinit var editMajor: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var changeButton: Button
    private lateinit var profileImage: ImageView

    private lateinit var myViewModel: MyViewModel
    private lateinit var imageFile: File
    private lateinit var imageFileURI: Uri
    private lateinit var tempImageFile: File
    private lateinit var tempImageFileURI: Uri

    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        initializeViews()


        val sharedPref = getSharedPreferences("MyRuns1Prefs", MODE_PRIVATE)
        initailizeViewsWithAlreadySavedData(sharedPref)


        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.profilePhoto.observe(this, Observer { bitmap ->
            profileImage.setImageBitmap(bitmap)
        })


        imageFile = File(getExternalFilesDir(null), "myProfilePhoto.jpg")
        imageFileURI = FileProvider.getUriForFile(this, "myRuns1Provider", imageFile)
        tempImageFile = File(getExternalFilesDir(null), "tempProfilePhoto.jpg")
        tempImageFileURI = FileProvider.getUriForFile(this, "myRuns1Provider", tempImageFile)


        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(tempImageFile.absolutePath)
                myViewModel.profilePhoto.value = bitmap
                myViewModel.profilePhotoUri = tempImageFileURI
            }
        }

        requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileURI)
                cameraResult.launch(intent)
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val bitmap = Util.getBitmap(this, uri)
                    myViewModel.profilePhoto.value = bitmap
                    contentResolver.openInputStream(uri)?.use { input ->
                        tempImageFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }

        val dialogFragmentTag = "dialogLauncher"

        var dialogLauncherFragment = supportFragmentManager.findFragmentByTag(dialogFragmentTag) as? UserActivity_Dialog_launcher

        if (dialogLauncherFragment == null) {
            dialogLauncherFragment = UserActivity_Dialog_launcher()
            supportFragmentManager.beginTransaction()
                .add(dialogLauncherFragment, dialogFragmentTag)
                .commitNow()
        }

        supportFragmentManager.setFragmentResultListener("selectedChoice", this) { _, bundle ->
            when (bundle.getInt("choice")) {
                0 -> launchCamera()
                1 -> launchGallery()
            }
        }

        saveButton.setOnClickListener {
            saveProfileInfo(sharedPref)
        }

        changeButton.setOnClickListener {
            dialogLauncherFragment.showDialog()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun initializeViews() {
        editName = findViewById(R.id.edit_name)
        editEmail = findViewById(R.id.edit_email)
        editPhone = findViewById(R.id.edit_phone)
        radioGroup = findViewById(R.id.radio_group)
        radioFemale = findViewById(R.id.radio_female)
        radioMale = findViewById(R.id.radio_male)
        editClass = findViewById(R.id.edit_class)
        editMajor = findViewById(R.id.edit_major)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)
        changeButton = findViewById(R.id.change_button)
        profileImage = findViewById(R.id.image_view)
    }

    private fun initailizeViewsWithAlreadySavedData(sharedPref: SharedPreferences) {
        editName.setText(sharedPref.getString("name", ""))
        editEmail.setText(sharedPref.getString("email", ""))
        editPhone.setText(sharedPref.getString("phone", ""))
        editClass.setText(sharedPref.getString("class", ""))
        editMajor.setText(sharedPref.getString("major", ""))
        sharedPref.getString("profile_image", null)?.let { uri ->
            profileImage.setImageURI(Uri.parse(uri))
        }
        when (sharedPref.getString("gender", "")) {
            "Female" -> radioFemale.isChecked = true
            "Male" -> radioMale.isChecked = true
        }
    }

    private fun launchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileURI)
            cameraResult.launch(intent)
        }
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResult.launch(intent)
    }

    private fun saveProfileInfo(sharedPref: SharedPreferences) {
        val editor = sharedPref.edit()
        editor.putString("name", editName.text.toString())
        editor.putString("email", editEmail.text.toString())
        editor.putString("phone", editPhone.text.toString())
        editor.putString("class", editClass.text.toString())
        editor.putString("major", editMajor.text.toString())

        if (tempImageFile.exists() && tempImageFile.length() > 0L) {
            tempImageFile.copyTo(imageFile, overwrite = true)
            myViewModel.profilePhotoUri = imageFileURI
            myViewModel.profilePhotoUri?.let { uri ->
                editor.putString("profile_image", uri.toString())
            }
        }

        val selectedGenderId = radioGroup.checkedRadioButtonId
        val gender = when (selectedGenderId) {
            R.id.radio_female -> "Female"
            R.id.radio_male -> "Male"
            else -> ""
        }
        editor.putString("gender", gender)
        editor.apply()

        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
