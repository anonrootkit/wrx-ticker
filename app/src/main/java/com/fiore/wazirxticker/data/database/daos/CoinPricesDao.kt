package com.fiore.wazirxticker.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiore.wazirxticker.data.database.entities.Coin

@Dao
interface CoinPricesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCoin(coin : Coin) : Long

    @Query("SELECT * FROM coins WHERE name=:coinName LIMIT 1")
    fun getCoin(coinName : String) : Coin?

    @Query("SELECT * FROM coins")
    fun getAllCoinsAtOnce(): List<Coin>

    @Query("SELECT * FROM coins")
    fun getAllCoins() : LiveData<List<Coin>>

    @Update
    suspend fun updateCoin(coin: Coin)
}