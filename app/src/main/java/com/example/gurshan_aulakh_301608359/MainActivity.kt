package com.example.gurshan_aulakh_301608359
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var startFragment: StartFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingsFragment: SettingsFragment
    private var tabTitles = arrayListOf("Start","History","Settings")

    //Got idea about fragments, viewPager, tabLayout and tabLayoutMediator from the lecture notes
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
        viewPager2.offscreenPageLimit=1

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy{
                tab, position -> tab.text = tabTitles[position]
        }
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
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

