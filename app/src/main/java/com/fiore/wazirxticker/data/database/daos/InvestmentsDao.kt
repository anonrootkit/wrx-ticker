package com.fiore.wazirxticker.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.Investment

@Dao
interface InvestmentsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insetInvestment(investment: Investment): Long

    @Query("SELECT * FROM investments WHERE name=:coinName")
    fun getInvestments(coinName: String): LiveData<List<Investment>>

    @Query("SELECT * FROM investments WHERE name=:coinName")
    fun getInvestmentsAtOnce(coinName: String): List<Investment>

    @Query("SELECT * FROM investments")
    fun getAllInvestmentsAtOnce(): List<Investment>

    @Query("SELECT * FROM investments")
    fun getAllInvestments(): LiveData<List<Investment>>

    @Update
    suspend fun updateInvestment(investment: Investment)

    @Query("DELETE FROM investments WHERE id=:investmentId")
    suspend fun deleteInvestment(investmentId : Long)
}