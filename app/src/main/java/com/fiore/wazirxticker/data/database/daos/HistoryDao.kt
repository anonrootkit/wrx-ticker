package com.fiore.wazirxticker.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.data.database.entities.Investment

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetHistory(history: History)

    @Query("SELECT * FROM history WHERE name=:coinName")
    fun getCoinHistory(coinName: String): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE name=:coinName")
    fun getCoinHistoryAtOnce(coinName: String): List<History>

    @Query("SELECT * FROM history")
    fun getEntireHistoryAtOnce(): List<History>

    @Query("SELECT * FROM history")
    fun getEntireHistory(): LiveData<List<History>>

    @Update
    suspend fun updateHistory(history: History)

    @Query("DELETE FROM history WHERE id=:id")
    suspend fun deleteHistory(id : Long)
}