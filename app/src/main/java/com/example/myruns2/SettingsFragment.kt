package com.example.myruns2

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var userProfilePref: Preference
    private lateinit var unitPreference: Preference
    private lateinit var webpagePref: Preference


    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        addPreferencesFromResource(R.xml.preference)
        val userProfilePref: Preference? = findPreference("userProfile")
        val unitPreference: Preference?= findPreference("unitPreference")
        val webpagePref: Preference?=findPreference("webpage")

        userProfilePref?.setOnPreferenceClickListener{
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            startActivity(intent)
            true
        }

        webpagePref?.setOnPreferenceClickListener{
            val url = "https://www.sfu.ca/computing.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
            true
        }
//        unitPreference?.setOnPreferenceClickListener{
//            val myDialog = MyRunsDialogFragment()
//            val bundle = Bundle()
//            myDialog.show(parentFragmentManager, "my dialog")
//
//            true
//        }
    }
}