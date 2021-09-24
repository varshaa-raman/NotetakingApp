package com.zohointerview.notetakingapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.AppBarLayout
import com.zohointerview.notetakingapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.note_preferences, rootKey)
    }
}