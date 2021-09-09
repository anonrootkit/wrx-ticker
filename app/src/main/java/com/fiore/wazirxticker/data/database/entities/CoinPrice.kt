package com.fiore.wazirxticker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "at_time")
    @SerializedName("at")
    val atTime: Long,

    @ColumnInfo(name = "coin_price")
    @SerializedName("ticker")
    val price: CoinPrice,

    @ColumnInfo(name = "show_coin_in_list")
    val showCoinInList: Boolean? = null
) {
    companion object {
        fun empty() = Coin(
            atTime = 0,
            price = CoinPrice.empty(),
            showCoinInList = null,
            name = ""
        )
    }
}

data class CoinPrice(
    @SerializedName("sell") // reversed because reversed in ticket api
    val buyPrice: String,
    @SerializedName("buy")  // reversed because reversed in ticket api
    val sellPrice: String,
    @SerializedName("vol")
    val volume: String
) {
    companion object {
        fun empty() = CoinPrice(
            buyPrice = "0",
            sellPrice = "0",
            volume = "0"
        )
    }
}

