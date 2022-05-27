package com.example.chatandstreamingapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.chatandstreamingapp.R
import java.util.*

open class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(newBase)
        val lang = sharedPref.getString(newBase.getString(R.string.language_pref), "es")
        val localeToSwitchTo = Locale(lang!!)
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

}