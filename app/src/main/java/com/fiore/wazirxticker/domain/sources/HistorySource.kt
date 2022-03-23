package com.fiore.wazirxticker.domain.sources

import androidx.lifecycle.LiveData
import com.fiore.wazirxticker.data.database.AppDatabase
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.data.database.entities.Investment
import com.fiore.wazirxticker.data.network.api.PricesAPI
import com.fiore.wazirxticker.domain.core.Request
import com.fiore.wazirxticker.domain.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistorySource @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun insertHistory(history: History) {
        withContext(Dispatchers.IO) {
            database.historyDao().insetHistory(history)
        }
    }

    suspend fun updateHistory(history: History) {
        withContext(Dispatchers.IO) {
            database.historyDao().updateHistory(history)
        }
    }

    suspend fun deleteHistory(id: Long) {
        withContext(Dispatchers.IO) {
            database.historyDao().deleteHistory(id)
        }
    }

    fun getAllHistory(): LiveData<List<History>> {
        return database.historyDao().getEntireHistory()
    }

    suspend fun getAllHistoryAtOnce(): List<History> {
        return withContext(Dispatchers.IO) { database.historyDao().getEntireHistoryAtOnce() }
    }

    fun getCoinHistory(name : String) : LiveData<List<History>> {
        return database.historyDao().getCoinHistory(name)
    }

    suspend fun getCoinHistoryAtOnce(name : String) : List<History> {
        return withContext(Dispatchers.IO){database.historyDao().getCoinHistoryAtOnce(name)}
    }
}