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
    val buyPrice: Float,

    @ColumnInfo(name = "buy_amount")
    val buyAmount: Long,

    @ColumnInfo(name = "profit_amount")
    val profitAmount: Long,

    @ColumnInfo(name = "profit_percent")
    val profitPercent: Float,

    @ColumnInfo(name = "total_coins")
    val totalCoins : Float,

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