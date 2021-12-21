@file:SuppressLint("NullSafeMutableLiveData")
@file:Suppress("unused", "SpellCheckingInspection")

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
import com.fiore.wazirxticker.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class PricesViewModel @Inject constructor(
    private val pricesSource: PricesSource,
    private val investmentsSource: InvestmentsSource
) : ViewModel() {

    private val coinHidden: MutableLiveData<String> = MutableLiveData()
    private var investmentDeleted: Investment? = null

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
                    fetchCoinDetails(it.name)
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
        it.filter {
            it.showCoinInList ?: true
        }
    }

    val investments = Transformations.map(investmentsSource.getAllInvestments()) {
        viewModelScope.launch {
            combinedInvestments(it)
        }
        it
    }

    private suspend fun combinedInvestments(investments: List<Investment>?) {
        if (investments.isNullOrEmpty()) {
            _combinedInvestments.value = ArrayList()
            return
        }

        val combinedInvestments = ArrayList<Investment>()

        val coinsName: HashSet<String> = HashSet()

        investments.forEach {
            coinsName.add(it.name.lowercase())
        }

        coinsName.forEach {
            val allInvestments = investmentsSource.getInvestmentsAtOnce(it)
            if (!allInvestments.isNullOrEmpty()) {
                combinedInvestments.add(getCombinedInvestment(investments = allInvestments))
            }
        }

        _combinedInvestments.value = combinedInvestments
    }

    private fun getCombinedInvestment(investments: List<Investment>): Investment {
        var totalProfitAmount = BigDecimal(0)
        var totalProfitPercent = BigDecimal(0)
        var totalBuyAmount = BigDecimal(0)
        var totalCoins = BigDecimal(0)

        investments.forEach {
            totalProfitAmount += it.profitAmount.bd()
            totalBuyAmount += it.buyAmount.bd()
            totalCoins += it.totalCoins.bd()
            totalProfitPercent += it.profitPercent.bd()
        }

        val investment = Investment(
            id = System.currentTimeMillis(),
            name = investments[0].name,
            buyAmount = totalBuyAmount.bis(),
            totalCoins = totalCoins.toString(),
            profitAmount = totalProfitAmount.bis(),
            profitPercent = totalProfitPercent.divide(
                investments.size.toBigDecimal(),
                2,
                RoundingMode.HALF_EVEN
            ).toPlainString(),
            isCombinedInvestment = true,
            buyPrice = "0",
            buyDate = 0
        )

        return investment
    }

    private val _combinedInvestments = MutableLiveData<List<Investment>>()
    val combinedInvestments: LiveData<List<Investment>>
        get() = _combinedInvestments

    private fun getCurrency(): String {
        return Currency.INR.value
    }

    suspend fun fetchCoinDetails(name: String, newCoin : Boolean = false) {
        _coinDetailsFetchStatus.value = ResponseStatus.LOADING

        var response: Result<Coin>? = null
        withContext(Dispatchers.IO) {
            response = pricesSource.getPrice(name + getCurrency())
        }

        if (response?.isSuccess() == true) {
            response!!.data?.let {
                val fetchedCoin = it.copy(name = name.lowercase())
                val coinInDB = getCoinFromDB(name)

                if (coinInDB != null) {
                    pricesSource.updateCoinInDB(fetchedCoin.copy(showCoinInList = if (newCoin) true else coinInDB.showCoinInList))
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

    suspend fun updateEachInvestment(investment: Investment) {
        val coin = pricesSource.getCoinFromDB(investment.name) ?: return
        updateInvestment(investment, coin, investment.buyAmount.bi(), investment.buyPrice.bd())
    }

    private suspend fun updateInvestment(
        investment: Investment,
        coin: Coin,
        buyAmount: BigInteger,
        buyPrice: BigDecimal
    ) {
        val updatedInvestment = investment.copy(
            profitPercent = calculateCurrentProfitPercent(
                coin,
                buyAmount,
                buyPrice
            ).toPlainString(),
            profitAmount = calculateCurrentPrice(coin, buyAmount, buyPrice).toString()
        )

        investmentsSource.updateInvestment(updatedInvestment)
    }

    fun insertInvestment(name: String, buyPrice: BigDecimal, buyAmount: BigInteger, date : Long) {
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

                        insertInvestmentInDB(fetchedCoin, buyAmount, buyPrice, date)
                    }
                }

                _addingInvestmentStatus.value = response.status
            } else {
                insertInvestmentInDB(coin, buyAmount, buyPrice, date)
                _addingInvestmentStatus.value = ResponseStatus.SUCCESS
            }
        }
    }

    private suspend fun insertInvestmentInDB(
        coin: Coin,
        buyAmount: BigInteger,
        buyPrice: BigDecimal,
        date : Long
    ) {
        val investment = Investment(
            id = System.currentTimeMillis(),
            name = coin.name,
            buyPrice = buyPrice.toPlainString(),
            buyAmount = buyAmount.toString(),
            profitAmount = calculateCurrentPrice(coin, buyAmount, buyPrice).toString(),
            profitPercent = calculateCurrentProfitPercent(
                coin,
                buyAmount,
                buyPrice
            ).toPlainString(),
            totalCoins = getTotalCoins(buyAmount, buyPrice).toPlainString(),
            buyDate = date
        )

        investmentsSource.insertInvestment(investment)
    }

    fun hideCoinInDB(coinName: String) {
        viewModelScope.launch {
            coinHidden.value = coinName
            pricesSource.updateCoinVisibilityStatus(coinName, show = false)
        }
    }

    fun undoCoinVisibilityStatus() {
        viewModelScope.launch {
            coinHidden.value?.let {
                pricesSource.updateCoinVisibilityStatus(name = it, show = true)
                coinHidden.value = null
            }
        }
    }

    fun deleteInvestmentFromDB(investment: Investment) {
        viewModelScope.launch {
            investmentDeleted = investment
            investmentsSource.deleteInvestment(investmentId = investment.id)
        }
    }

    fun undoDeleteInvestment() {
        viewModelScope.launch {
            investmentDeleted?.let {
                investmentsSource.insertInvestment(it)
                investmentDeleted = null
            }
        }
    }
}

enum class Currency(val value: String) {
    INR("inr"),
    USDT("usdt")
}