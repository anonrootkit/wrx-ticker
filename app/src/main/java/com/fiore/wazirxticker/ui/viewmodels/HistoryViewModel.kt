package com.fiore.wazirxticker.ui.viewmodels

import androidx.lifecycle.*
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.data.database.entities.Investment
import com.fiore.wazirxticker.domain.core.ResponseStatus
import com.fiore.wazirxticker.domain.sources.HistorySource
import com.fiore.wazirxticker.domain.sources.PricesSource
import com.fiore.wazirxticker.utils.bd
import com.fiore.wazirxticker.utils.calculateHistoryProfitAmountAndPercentage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historySource: HistorySource,
    private val pricesSource: PricesSource
) : ViewModel(){

    private val _addingHistoryStatus = MutableLiveData<ResponseStatus>()
    val addingHistoryStatus: LiveData<ResponseStatus>
        get() = _addingHistoryStatus

    private var lastHistoryIdInserted = 0L
    private var lastHistoryDeleted : History? = null

    val history = Transformations.map(historySource.getAllHistory()) {
        it
    }

    fun undoHistoryInsertion() {
        if (lastHistoryIdInserted > 0L) {
            deleteHistory(lastHistoryIdInserted)
            lastHistoryIdInserted = 0
        }
    }

    fun undoHistoryDeletion() {
        lastHistoryDeleted?.let {
            viewModelScope.launch { historySource.insertHistory(it) }
            lastHistoryDeleted = null
        }
    }

    fun insertInvestmentToHistory(investment: Investment) {
        viewModelScope.launch {
            val coinCurrentPrice = pricesSource.getCoinFromDB(investment.name)?.price?.buyPrice?.bd() ?: return@launch

            lastHistoryIdInserted = insertHistory(
                historyId = System.currentTimeMillis(),
                name = investment.name.uppercase(),
                buyDate = investment.buyDate,
                sellDate = System.currentTimeMillis(),
                buyPrice = investment.buyPrice.bd(),
                sellPrice = coinCurrentPrice,
                coinsCount = investment.totalCoins.bd()
            )
        }
    }

    fun insertHistory(
        historyId : Long,
        name : String,
        buyDate : Long,
        sellDate : Long,
        buyPrice : BigDecimal,
        sellPrice : BigDecimal,
        coinsCount : BigDecimal
    ) : Long {
        _addingHistoryStatus.value = ResponseStatus.LOADING
        val (profitAmount, profitPercentage) = calculateHistoryProfitAmountAndPercentage(buyPrice, sellPrice, coinsCount)

        val history = History(
            id = historyId,
            name = name,
            buyDate = buyDate,
            sellDate = sellDate,
            buyPrice = buyPrice.toPlainString(),
            sellPrice = sellPrice.toPlainString(),
            coinsCount = coinsCount.toPlainString(),
            profitAmount = profitAmount,
            profitPercent = profitPercentage,
            amountInvested = buyPrice.multiply(coinsCount).divide(1.toBigDecimal(), 2, BigDecimal.ROUND_HALF_EVEN).toPlainString(),
            amountReceived = sellPrice.multiply(coinsCount).divide(1.toBigDecimal(), 2, BigDecimal.ROUND_HALF_EVEN).toPlainString(),
        )

        viewModelScope.launch {
            historySource.insertHistory(history)
            _addingHistoryStatus.value = ResponseStatus.SUCCESS
        }

        return historyId
    }

    private fun deleteHistory(historyId : Long) {
        viewModelScope.launch {
            historySource.deleteHistory(historyId)
        }
    }

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            deleteHistory(historyId = history.id)
            lastHistoryDeleted = history
        }
    }

}