package com.fiore.wazirxticker.domain.sources

import com.fiore.wazirxticker.data.preferences.PreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GeneralSource @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
) {
    suspend fun clearStorageData() {
        withContext(Dispatchers.IO) {
            preferenceStorage.clearData()
        }
    }

    fun setCurrentAppTheme(theme: String) = preferenceStorage.setCurrentAppTheme(theme)

    fun getCurrentAppTheme(): String = preferenceStorage.getCurrentAppTheme()

    fun setUpdateAvailable(updateAvailable: Boolean) = preferenceStorage.setUpdateAvailable(updateAvailable)

    fun getUpdateAvailable(): Boolean = preferenceStorage.getUpdateAvailable()
}