package com.fiore.wazirxticker.data.database.entities

import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "investments",
    indices = [
        Index("name", unique = false)
    ]
)
data class Investment(
    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "buy_price")
    val buyPrice: String,

    @ColumnInfo(name = "buy_amount")
    val buyAmount: String,

    @ColumnInfo(name = "profit_amount")
    val profitAmount: String,

    @ColumnInfo(name = "profit_percent")
    val profitPercent: String,

    @ColumnInfo(name = "total_coins")
    val totalCoins : String,

    @ColumnInfo(name = "is_combined_investment")
    val isCombinedInvestment : Boolean = false
){
    fun formattedTotalCoins() = "Coins : $totalCoins"
    fun formattedProfitAmount() = "₹$profitAmount"
    fun formattedProfitPercent() = "$profitPercent%"
    fun formattedBuyAmount() = "₹$buyAmount"
    fun formattedBuyPrice() = "@ ₹$buyPrice/${name.uppercase()}"
    fun getItemVisibility() = if (isCombinedInvestment) View.GONE else View.VISIBLE
}