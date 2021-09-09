package com.fiore.wazirxticker.data.database.converters

import androidx.room.TypeConverter
import com.fiore.wazirxticker.data.database.entities.CoinPrice
import com.google.gson.Gson


class Converters {
    @TypeConverter
    fun fromChatPrice(coinPrice: CoinPrice): String =
        Gson().toJson(coinPrice)

    @TypeConverter
    fun toChatPrice(coinPrice: String): CoinPrice =
        Gson().fromJson(coinPrice, CoinPrice::class.java)
}
