package com.fiore.wazirxticker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.fiore.wazirxticker.domain.sources.GeneralSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UtilsViewModel
@Inject constructor(private val generalSource: GeneralSource) : ViewModel() {

    fun setCurrentAppTheme(theme: String) = generalSource.setCurrentAppTheme(theme)

    fun getCurrentTheme() = generalSource.getCurrentAppTheme()
}