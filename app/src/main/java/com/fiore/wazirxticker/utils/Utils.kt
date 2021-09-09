package com.fiore.wazirxticker.utils

import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.domain.core.ResponseStatus
import kotlin.math.roundToLong

fun getErrorType(errorMsg: String): ResponseStatus {
    return try {
        when (errorMsg.substring(5, 8).toInt()) {
            400 -> ResponseStatus.Server_400_ERROR
            401 -> ResponseStatus.Server_401_ERROR
            403 -> ResponseStatus.Server_403_ERROR
            404 -> ResponseStatus.Server_404_ERROR
            else -> ResponseStatus.SERVER_ERROR
        }
    } catch (_: Exception) {
        ResponseStatus.SERVER_ERROR
    }
}

fun getErrorTypeFromResponseCode(code: Int): ResponseStatus {
    return when (code) {
        400 -> ResponseStatus.Server_400_ERROR
        401 -> ResponseStatus.Server_401_ERROR
        403 -> ResponseStatus.Server_403_ERROR
        404 -> ResponseStatus.Server_404_ERROR
        else -> ResponseStatus.SERVER_ERROR
    }
}

fun calculateCurrentPrice(coin: Coin, buyAmount : Long, buyPrice : Float) : Long{
    val currentPricePerCoin = coin.price.sellPrice.toFloat()
    return (currentPricePerCoin * getTotalCoins(buyAmount, buyPrice)).roundToLong()
}

fun calculateCurrentProfitPercent(coin: Coin, buyAmount : Long, buyPrice : Float) : Float {
    val currentPricePerCoin = coin.price.sellPrice.toFloat()
    val currentAmount = (currentPricePerCoin * getTotalCoins(buyAmount, buyPrice))
    return ((currentAmount/buyAmount)*100) - 100
}

fun getTotalCoins(buyAmount: Long, buyPrice: Float): Float {
    return (buyAmount / buyPrice)
}


fun validatePrice(price : String) : Boolean{
    return try {
        price.toFloat() > 0
    }catch (e : Exception) {
        false
    }
}