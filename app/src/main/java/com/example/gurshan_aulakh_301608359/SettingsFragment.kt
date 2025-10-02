package com.example.gurshan_aulakh_301608359

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var userProfilePref: Preference
    private lateinit var unitPreference: Preference
    private lateinit var webpagePref: Preference

//read the instructions for myruns2 to understand that i have to use PreferenceFragmentCompat() for settings.
    //I took help from gpt to undertsand the importance of Prferences but wrote the code by myself after understanding
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

    }
}