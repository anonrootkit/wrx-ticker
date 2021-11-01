package com.fiore.wazirxticker.ui

import android.app.Application
import com.fiore.wazirxticker.domain.sources.GeneralSource
import com.fiore.wazirxticker.utils.changeAppTheme
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TickerApplication : Application() {

    @Inject
    lateinit var generalSource: GeneralSource

    override fun onCreate() {
        super.onCreate()

        checkCurrentAppTheme()
    }

    private fun checkCurrentAppTheme() {
        val theme = generalSource.getCurrentAppTheme()
        changeAppTheme(theme)
    }
}