package com.example.chatandstreamingapp.ui.preferences

import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceFragmentCompat
import com.example.chatandstreamingapp.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onDestroy() {
        requireActivity().recreate()
        super.onDestroy()
    }
}