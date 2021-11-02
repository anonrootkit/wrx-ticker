package com.fiore.wazirxticker.domain.sources

import androidx.lifecycle.LiveData
import com.fiore.wazirxticker.data.database.AppDatabase
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.Investment
import com.fiore.wazirxticker.data.network.api.PricesAPI
import com.fiore.wazirxticker.domain.core.Request
import com.fiore.wazirxticker.domain.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvestmentsSource @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun insertInvestment(investment: Investment) {
        withContext(Dispatchers.IO) {
            database.investmentsDao().insetInvestment(investment)
        }
    }

    suspend fun updateInvestment(investment: Investment) {
        withContext(Dispatchers.IO) {
            database.investmentsDao().updateInvestment(investment)
        }
    }

    suspend fun deleteInvestment(investmentId: Long) {
        withContext(Dispatchers.IO) {
            database.investmentsDao().deleteInvestment(investmentId)
        }
    }

    fun getAllInvestments(): LiveData<List<Investment>> {
        return database.investmentsDao().getAllInvestments()
    }

    suspend fun getAllInvestmentsAtOnce(): List<Investment> {
        return withContext(Dispatchers.IO) { database.investmentsDao().getAllInvestmentsAtOnce() }
    }

    fun getInvestments(name : String) : LiveData<List<Investment>> {
        return database.investmentsDao().getInvestments(name)
    }

    suspend fun getInvestmentsAtOnce(name : String) : List<Investment> {
        return withContext(Dispatchers.IO){database.investmentsDao().getInvestmentsAtOnce(name)}
    }
}