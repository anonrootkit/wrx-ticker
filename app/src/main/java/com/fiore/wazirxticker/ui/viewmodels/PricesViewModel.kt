package com.fiore.wazirxticker.ui.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.domain.sources.PricesSource
import com.fiore.wazirxticker.utils.timberD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PricesViewModel @Inject constructor(
    private val pricesSource: PricesSource
) : ViewModel() {

    private val _coinDetailsFetchStatus = MutableLiveData<ResponseStatus>()
    val coinDetailsFetchStatus : LiveData<ResponseStatus>
        get() = _coinDetailsFetchStatus

    private var updatingCoins = false

    private val coinsUpdateTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            coins.value?.forEach {
                fetchCoinDetails(it.name.lowercase())
            }
        }

        override fun onFinish() {
        }
    }

    fun startUpdatingCoins() {
        if (updatingCoins) return
        coinsUpdateTimer.start()
        updatingCoins = true
    }

    val coins = Transformations.map(pricesSource.getAllCoins()) {
        it
    }

    fun fetchCoinDetails(name: String, currency: String = Currency.INR.value) {
        viewModelScope.launch {
            _coinDetailsFetchStatus.value = ResponseStatus.LOADING
            val response = pricesSource.getPrice(name+currency)

            if(response.isSuccess()) {
                response.data?.let {
                    val fetchedCoin = it.copy(name = name.lowercase())
                    val coinInDB = getCoinFromDB(name)

                    if (coinInDB != null) {
                        pricesSource.updateCoinInDB(fetchedCoin.copy(showCoinInList = coinInDB.showCoinInList))
                    }else{
                        pricesSource.insertCoinInDB(fetchedCoin.copy(showCoinInList = true))
                    }
                }
            }

            _coinDetailsFetchStatus.value = response.status
        }
    }

    fun resetCoinDetailsFetchStatus(){
        _coinDetailsFetchStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        coinsUpdateTimer.cancel()
    }

    private suspend fun getCoinFromDB(name: String) : Coin? {
        return pricesSource.getCoinFromDB(name)
    }

}

enum class Currency(val value: String) {
    INR("inr"),
    USD("usd")
}