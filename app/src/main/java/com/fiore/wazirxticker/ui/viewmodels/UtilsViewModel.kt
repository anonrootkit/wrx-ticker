package com.fiore.wazirxticker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fiore.wazirxticker.domain.sources.GeneralSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UtilsViewModel
@Inject constructor(private val generalSource: GeneralSource) : ViewModel() {

    private val _dismissInvestmentsBottomSheet = MutableLiveData<Boolean>()
    val dismissInvestmentsBottomSheet : LiveData<Boolean>
        get() = _dismissInvestmentsBottomSheet

    fun setCurrentAppTheme(theme: String) = generalSource.setCurrentAppTheme(theme)

    fun getCurrentTheme() = generalSource.getCurrentAppTheme()

    fun updateDismissInvestmentBottomSheet(dismiss : Boolean) {
        _dismissInvestmentsBottomSheet.value = dismiss
    }

    fun setUpdateAvailable(updateAvailable: Boolean) {
        generalSource.setUpdateAvailable(updateAvailable)
    }

    fun getUpdateAvailable(): Boolean = generalSource.getUpdateAvailable()
}