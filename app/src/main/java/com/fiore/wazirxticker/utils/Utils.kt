package com.fiore.wazirxticker.utils

import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.domain.core.ResponseStatus
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
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

fun calculateCurrentPrice(coin: Coin, buyAmount : BigInteger, buyPrice : BigDecimal) : BigInteger {
    val currentPricePerCoin = coin.price.sellPrice.bd()
    return (currentPricePerCoin * getTotalCoins(buyAmount, buyPrice)).toBigInteger()
}

fun calculateCurrentProfitPercent(coin: Coin, buyAmount : BigInteger, buyPrice : BigDecimal) : BigDecimal {
    val currentPricePerCoin = coin.price.sellPrice.bd()
    val currentAmount : BigDecimal = (currentPricePerCoin * getTotalCoins(buyAmount, buyPrice))
    return (((currentAmount/buyAmount.toBigDecimal())* BigDecimal(100)) - BigDecimal(100)).divide(1.toBigDecimal(), 2, RoundingMode.HALF_EVEN)
}

fun getTotalCoins(buyAmount: BigInteger, buyPrice: BigDecimal): BigDecimal {
    return buyAmount.toBigDecimal().divide(buyPrice, 2, RoundingMode.HALF_EVEN)
}


fun validatePrice(price : String) : Boolean{
    return try {
        price.toFloat() > 0
    }catch (e : Exception) {
        false
    }
}

fun calculateHistoryProfitAmountAndPercentage(buyAmount : BigDecimal, sellAmount : BigDecimal, coinsCount : BigDecimal) : Pair<String, String> {
    val coinsBoughtAt = buyAmount.multiply(coinsCount)
    val coinsSoldAt = sellAmount.multiply(coinsCount)
    val profitAmount = coinsSoldAt.subtract(coinsBoughtAt).divide(1.toBigDecimal(), 2, RoundingMode.HALF_EVEN)
    val profitPercentage = coinsSoldAt.divide(coinsBoughtAt, 2, RoundingMode.HALF_EVEN).multiply(100.toBigDecimal()).subtract(100.toBigDecimal())
    return profitAmount.toPlainString() to profitPercentage.toPlainString()
}