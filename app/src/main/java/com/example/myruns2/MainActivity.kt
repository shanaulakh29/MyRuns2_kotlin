package com.example.myruns2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myruns2.ui.theme.MyRuns2Theme
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var startFragment: StartFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment
    private var tabTitles = arrayListOf("Start","History","Settings")

    fun setUpFragments(){
        viewPager2 = findViewById<ViewPager2>(R.id.viewpager)
        tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        startFragment = StartFragment()
        historyFragment = HistoryFragment()
        settingsFragment = SettingsFragment()
        val fragmentsList = ArrayList<Fragment>()
        fragmentsList.add(startFragment)
        fragmentsList.add(historyFragment)
        fragmentsList.add(settingsFragment)
        val viewPagerAdapter = ViewPagerAdapter(this, fragmentsList)
        viewPager2.adapter = viewPagerAdapter

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy{
                tab, position -> tab.text = tabTitles[position]
        }
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }
    val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), { isGranted ->
        })

    fun getCameraPermission(){
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        getCameraPermission()
        Util.checkPermissions(this)
        setUpFragments()
    }


}

