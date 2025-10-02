package com.example.gurshan_aulakh_301608359

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class UserActivity_Dialog_launcher : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun showDialog() {
        val dialog = MyRunsDialogFragment()
        val args = Bundle()
        args.putInt(MyRunsDialogFragment.DIALOG_KEY, 8)
        dialog.arguments = args
        dialog.show(parentFragmentManager, "UserProfileDialog")
    }


}