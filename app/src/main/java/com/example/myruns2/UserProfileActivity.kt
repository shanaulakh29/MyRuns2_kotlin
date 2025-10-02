package com.example.myruns2

import android.Manifest
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myruns2.MyViewModel
import java.io.File
import java.io.OutputStream

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

    fun initializeViews(){
        editName = findViewById<EditText>(R.id.edit_name)
        editEmail = findViewById<EditText>(R.id.edit_email)
        editPhone = findViewById<EditText>(R.id.edit_phone)
        radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioFemale = findViewById<RadioButton>(R.id.radio_female)
        radioMale = findViewById<RadioButton>(R.id.radio_male)
        editClass = findViewById<EditText>(R.id.edit_class)
        editMajor = findViewById<EditText>(R.id.edit_major)
        saveButton = findViewById<Button>(R.id.save_button)
        cancelButton = findViewById<Button>(R.id.cancel_button)
        changeButton = findViewById<Button>(R.id.change_button)
        profileImage = findViewById<ImageView>(R.id.image_view)
    }
    fun initailizeViewsWithAlreadySavedData(sharedPref: SharedPreferences){
        editName.setText(sharedPref.getString("name",""))
        editEmail.setText(sharedPref.getString("email",""))
        editPhone.setText(sharedPref.getString("phone",""))
        editClass.setText(sharedPref.getString("class",""))
        editMajor.setText(sharedPref.getString("major",""))
        val savedImageUri = sharedPref.getString("profile_image",null)
        if(savedImageUri!=null){
            profileImage.setImageURI(Uri.parse(savedImageUri))
        }

        val savedGender = sharedPref.getString("gender","");
        if(savedGender=="Female"){
            radioFemale.isChecked=true
        }else if(savedGender == "Male"){
            radioMale.isChecked=true
        }
    }
    fun saveProfileInfo(
        sharedPref: SharedPreferences,
        myViewModel: MyViewModel,
        imageFile: File,
        imageFileURI: Uri,
        tempImageFile: File
    ){
        val editor = sharedPref.edit()
        editor.putString("name",editName.text.toString())
        editor.putString("email",editEmail.text.toString())
        editor.putString("phone",editPhone.text.toString())
        editor.putString("class",editClass.text.toString())
        editor.putString("major",editMajor.text.toString())
        if(tempImageFile.exists() && tempImageFile.length() > 0L){
            tempImageFile.copyTo(imageFile, true)
            myViewModel.profilePhotoUri=imageFileURI
            myViewModel.profilePhotoUri?.let{ newImageURI->
                editor.putString("profile_image", newImageURI.toString())
            }
        }
        val gender: String
        val selectedGenderId = radioGroup.checkedRadioButtonId
        if(selectedGenderId==R.id.radio_female){
            gender="Female"
        }else if(selectedGenderId==R.id.radio_male){
            gender="Male"
        }else{
            gender=""
        }
        editor.putString("gender",gender)
        editor.apply()
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        initializeViews()
        val sharedPref = getSharedPreferences("MyRuns1Prefs", MODE_PRIVATE)
        initailizeViewsWithAlreadySavedData(sharedPref)
        val myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.profilePhoto.observe(this, Observer { newImage ->
            profileImage.setImageBitmap(newImage)
        })
        //Got idea from lecture notes about how to use FileProvider
        val imageFile = File(getExternalFilesDir(null), "myProfilePhoto.jpg")
        val imageFileURI = FileProvider.getUriForFile(this, "myRuns1Provider", imageFile)
        val tempImageFile = File(getExternalFilesDir(null), "tempProfilePhoto.jpg")
        val tempImageFileURI = FileProvider.getUriForFile(this, "myRuns1Provider", tempImageFile)

        //Read the course material under Pages->The Phone Camera and Data Storge section to understand how to open the camera and write a callback
        val cameraResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeFile(tempImageFile.absolutePath)
                    myViewModel.profilePhoto.value = bitmap
                    myViewModel.profilePhotoUri = tempImageFileURI
                }
            })

        val requestCameraPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission(), { isGranted ->
                if (isGranted) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileURI)
                    cameraResult.launch(intent)
                }
            })


        val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val uri = result.data?.data
                        ?: throw IllegalStateException("Gallery result returned null URI")

                    val bitmap = Util.getBitmap(this, uri)
                    myViewModel.profilePhoto.value = bitmap
                    contentResolver.openInputStream(uri)?.use { input ->
                        tempImageFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("gallery error", e.message ?: "Unknown error")
                }
            }
        }
        fun launchCamera() {
            if (ContextCompat.checkSelfPermission(
                    this@UserProfileActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                //read from the course lecture material about how to create implicit intent
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileURI)
                cameraResult.launch(intent)
            }
        }

        fun launchGallery() {
            val intent =Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryResult.launch(intent)
            }

        saveButton.setOnClickListener {
            saveProfileInfo(sharedPref, myViewModel, imageFile, imageFileURI, tempImageFile)

        }
        changeButton.setOnClickListener {
            val dialog = MyRunsDialogFragment()
            val args = Bundle()
            args.putInt(MyRunsDialogFragment.DIALOG_KEY, 8)
            dialog.arguments = args
            dialog.show(supportFragmentManager, "UserProfileDialog")
            supportFragmentManager.setFragmentResultListener(
                "selectedChoice",
                this
            ) { requestKey, bundle ->
                when (bundle.getInt("choice")) {
                    0 -> launchCamera()
                    1 -> launchGallery()
                }
            }
        }

        cancelButton.setOnClickListener {
            //    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}

//        val requestGalleryPermission =
//            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                if (isGranted) {
////                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////                galleryResult.launch(intent)
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.type = "image/*"
//                    galleryResult.launch(intent)
//                }
//            }
//        val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val uri = result.data!!.data!!
//                val bitmap = Util.getBitmap(this, uri)
//                myViewModel.profilePhoto.value = bitmap
//                val input = contentResolver.openInputStream(uri)
//                val output = tempImageFile.outputStream()
//                input?.copyTo(output)
//                input?.close()
//                output.close()
//            }
//        }

//fun handleChangeButton(
//    requestCameraPermission: ActivityResultLauncher<String>,
//    requestGalleryPermission: ActivityResultLauncher<String>,
//    cameraResult: ActivityResultLauncher<Intent>,
//    tempImageFileURI: Uri,
//    galleryResult: ActivityResultLauncher<Intent>
//) {
//
//}