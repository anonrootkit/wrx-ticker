package com.fiore.wazirxticker.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fiore.wazirxticker.utils.getDate
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "history",
    indices = [
        Index("name", unique = false)
    ]
)
data class History(
    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "buy_price")
    val buyPrice: String,

    @ColumnInfo(name = "sell_price")
    val sellPrice: String,

    @ColumnInfo(name = "coins_count")
    val coinsCount: String,

    @ColumnInfo(name = "profit_amount")
    val profitAmount: String,

    @ColumnInfo(name = "profit_percent")
    val profitPercent: String,

    @ColumnInfo(name = "buy_date")
    val buyDate : Long,

    @ColumnInfo(name = "sell_date")
    val sellDate : Long,

    @ColumnInfo(name = "amount_invested")
    val amountInvested : String,

    @ColumnInfo(name = "amount_received")
    val amountReceived : String,
) : Parcelable {
    fun formattedBuyDate() = buyDate.getDate("dd/MM/YYYY")
    fun formattedSellDate() = sellDate.getDate("dd/MM/YYYY")
    fun formattedBoughtAt() = "$buyPrice/$name"
    fun formattedSoldAt() = "$sellPrice/$name"

    fun formattedAmountInvested() = "₹$amountInvested"
    fun formattedAmountReceived() = "₹$amountReceived"
    fun formattedProfitAndPercent() = "(${if (profitAmount.toBigDecimal() > 0.toBigDecimal()) "+" else ""}$profitAmount / ${if (profitAmount.toBigDecimal() > 0.toBigDecimal()) "+" else ""}$profitPercent%)"
}