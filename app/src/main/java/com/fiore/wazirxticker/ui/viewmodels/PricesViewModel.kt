@file:SuppressLint("NullSafeMutableLiveData")
package com.fiore.wazirxticker.ui.viewmodels

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.*
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.Investment
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.domain.core.Result
import com.fiore.wazirxticker.domain.sources.InvestmentsSource
import com.fiore.wazirxticker.domain.sources.PricesSource
import com.fiore.wazirxticker.utils.calculateCurrentPrice
import com.fiore.wazirxticker.utils.calculateCurrentProfitPercent
import com.fiore.wazirxticker.utils.getTotalCoins
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PricesViewModel @Inject constructor(
    private val pricesSource: PricesSource,
    private val investmentsSource: InvestmentsSource
) : ViewModel() {

    private val _coinDetailsFetchStatus = MutableLiveData<ResponseStatus>()
    val coinDetailsFetchStatus: LiveData<ResponseStatus>
        get() = _coinDetailsFetchStatus

    private val _addingInvestmentStatus = MutableLiveData<ResponseStatus>()
    val addingInvestmentStatus: LiveData<ResponseStatus>
        get() = _addingInvestmentStatus

    private var updatingCoins = false
    private var updatingInvestments = false

    private val coinsUpdateTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            coins.value?.forEach {
                viewModelScope.launch {
                    fetchCoinDetails(it.name.lowercase())
                }
            }
        }

        override fun onFinish() {
        }
    }

    private val investmentUpdateTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            investments.value?.forEach {
                viewModelScope.launch {
                    updateEachInvestment(it)
                }
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

    fun startUpdatingInvestments() {
        if (updatingInvestments) return
        investmentUpdateTimer.start()
        updatingInvestments = true
    }

    val coins = Transformations.map(pricesSource.getAllCoins()) {
        it
    }

    val investments = Transformations.map(investmentsSource.getAllInvestments()) {
        viewModelScope.launch {
            combinedInvestments(it)
        }
        it
    }

    private suspend fun combinedInvestments(investments: List<Investment>?) {
        if (investments.isNullOrEmpty()) return

        val combinedInvestments = ArrayList<Investment>()

        val coinsName : HashSet<String> = HashSet()

        investments.forEach {
            coinsName.add(it.name.lowercase())
        }

        coinsName.forEach {
            val allInvestments = investmentsSource.getInvestmentsAtOnce(it)
            if (!allInvestments.isNullOrEmpty()){
                combinedInvestments.add(getCombinedInvestment(investments = allInvestments))
            }
        }

        _combinedInvestments.value = combinedInvestments
    }

    private fun getCombinedInvestment(investments: List<Investment>): Investment {
        var totalProfitAmount : Float = 0f
        var totalProfitPercent : Float = 0f
        var totalBuyAmount : Float = 0f
        var totalCoins : Float = 0f

        investments.forEach {
            totalProfitAmount += it.profitAmount
            totalBuyAmount += it.buyAmount
            totalCoins += it.totalCoins
            totalProfitPercent += it.profitPercent
        }

        val investment = Investment(
            id = System.currentTimeMillis(),
            name = investments[0].name,
            buyAmount = totalBuyAmount.toLong(),
            totalCoins = totalCoins,
            profitAmount = totalProfitAmount.toLong(),
            profitPercent = totalProfitPercent / investments.size,
            isCombinedInvestment = true,
            buyPrice = 0f
        )

        return investment
    }

    private val _combinedInvestments = MutableLiveData<List<Investment>>()
    val combinedInvestments : LiveData<List<Investment>>
        get() = _combinedInvestments

    private fun getCurrency() : String {
        return Currency.INR.value
    }

    suspend fun fetchCoinDetails(name: String) {
        _coinDetailsFetchStatus.value = ResponseStatus.LOADING
        var response : Result<Coin>? = null
        withContext(Dispatchers.IO) {
            response = pricesSource.getPrice(name + getCurrency())
        }

        if (response?.isSuccess() == true) {
            response!!.data?.let {
                val fetchedCoin = it.copy(name = name.lowercase())
                val coinInDB = getCoinFromDB(name)

                if (coinInDB != null) {
                    pricesSource.updateCoinInDB(fetchedCoin.copy(showCoinInList = coinInDB.showCoinInList))
                } else {
                    pricesSource.insertCoinInDB(fetchedCoin.copy(showCoinInList = true))
                }
            }
        }

        _coinDetailsFetchStatus.value = response?.status
    }

    fun resetCoinDetailsFetchStatus() {
        _coinDetailsFetchStatus.value = null
    }

    fun resetInvestmentStatus() {
        _addingInvestmentStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        coinsUpdateTimer.cancel()
        investmentUpdateTimer.cancel()
    }

    private suspend fun getCoinFromDB(name: String): Coin? {
        return pricesSource.getCoinFromDB(name)
    }

    suspend fun updateEachInvestment(investment : Investment) {
        val coin = pricesSource.getCoinFromDB(investment.name) ?: return
        updateInvestment(investment, coin, investment.buyAmount, investment.buyPrice)
    }

    private suspend fun updateInvestment(investment: Investment, coin: Coin, buyAmount: Long, buyPrice: Float) {
        val updatedInvestment = investment.copy(
            profitPercent = calculateCurrentProfitPercent(coin, buyAmount, buyPrice),
            profitAmount = calculateCurrentPrice(coin, buyAmount, buyPrice)
        )

        investmentsSource.updateInvestment(updatedInvestment)
    }

    fun insertInvestment(name: String, buyPrice: Float, buyAmount: Long) {
        viewModelScope.launch {
            _addingInvestmentStatus.value = ResponseStatus.LOADING
            val coin = pricesSource.getCoinFromDB(name)

            if (coin == null) {
                val response = pricesSource.getPrice(name + getCurrency())

                if (response.isSuccess()) {
                    response.data?.let {
                        val fetchedCoin = it.copy(name = name.lowercase())
                        val coinInDB = getCoinFromDB(name)

                        if (coinInDB != null) {
                            pricesSource.updateCoinInDB(fetchedCoin.copy(showCoinInList = coinInDB.showCoinInList))
                        } else {
                            pricesSource.insertCoinInDB(fetchedCoin.copy(showCoinInList = true))
                        }

                        insertInvestmentInDB(fetchedCoin, buyAmount, buyPrice)
                    }
                }

                _addingInvestmentStatus.value = response.status
            } else {
                insertInvestmentInDB(coin, buyAmount, buyPrice)
                _addingInvestmentStatus.value = ResponseStatus.SUCCESS
            }
        }
    }

    private suspend fun insertInvestmentInDB(coin: Coin, buyAmount: Long, buyPrice: Float) {
        val investment = Investment(
            id = System.currentTimeMillis(),
            name = coin.name,
            buyPrice = buyPrice,
            buyAmount = buyAmount,
            profitAmount = calculateCurrentPrice(coin, buyAmount, buyPrice),
            profitPercent = calculateCurrentProfitPercent(coin, buyAmount, buyPrice),
            totalCoins = getTotalCoins(buyAmount, buyPrice)
        )

        investmentsSource.insertInvestment(investment)
    }

}

enum class Currency(val value: String) {
    INR("inr"),
    USD("usd")
}