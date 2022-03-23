@file:SuppressLint("CommitPrefEdits", "ApplySharedPref")

package com.fiore.wazirxticker.data.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.fiore.wazirxticker.utils.PreferencesConstants.APP_THEME
import com.fiore.wazirxticker.utils.PreferencesConstants.UPDATE_AVAILABLE
import com.fiore.wazirxticker.utils.ThemeConstants
import javax.inject.Inject


class PreferenceStorage @Inject constructor(private val preferences: SharedPreferences) {

    fun setCurrentAppTheme(theme: String) {
        preferences.edit().putString(APP_THEME, theme).commit()
    }

    fun getCurrentAppTheme(): String {
        return preferences.getString(APP_THEME, ThemeConstants.DARK) ?: ThemeConstants.DARK
    }

    fun setUpdateAvailable(updateAvailable: Boolean) {
        preferences.edit().putBoolean(UPDATE_AVAILABLE, updateAvailable).commit()
    }

    fun getUpdateAvailable(): Boolean {
        return preferences.getBoolean(UPDATE_AVAILABLE, false)
    }

    fun clearData() {
        preferences.edit().clear().apply()
    }
}