package com.fiore.wazirxticker.domain.sources

import androidx.lifecycle.LiveData
import com.fiore.wazirxticker.data.database.AppDatabase
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.network.api.PricesAPI
import com.fiore.wazirxticker.domain.core.Request
import com.fiore.wazirxticker.domain.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PricesSource @Inject constructor(
    private val request: Request,
    private val pricesAPI: PricesAPI,
    private val database: AppDatabase
) {
    suspend fun getPrice(
        coinWithCurrency: String
    ): Result<Coin> {
        return request.normalRequest(
            call = pricesAPI.getPrice(coinWithCurrency),
            default = Coin.empty()
        )
    }

    suspend fun insertCoinInDB(coin: Coin) {
        withContext(Dispatchers.IO) {
            database.coinPricesDao().insertCoin(coin)
        }
    }

    suspend fun updateCoinInDB(coin: Coin) {
        withContext(Dispatchers.IO) {
            database.coinPricesDao().updateCoin(coin)
        }
    }

    suspend fun getAllCoinsAtOnce(): List<Coin> {
        return withContext(Dispatchers.IO) { database.coinPricesDao().getAllCoinsAtOnce() }
    }

    fun getAllCoins(): LiveData<List<Coin>> {
        return database.coinPricesDao().getAllCoins()
    }

    suspend fun getCoinFromDB(name: String): Coin? {
        return withContext(Dispatchers.IO) { database.coinPricesDao().getCoin(name) }
    }
}