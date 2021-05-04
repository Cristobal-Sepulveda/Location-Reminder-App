package com.udacity.project4.utils

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider


object sharedPreference {
    val key = "user_logged"
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
}