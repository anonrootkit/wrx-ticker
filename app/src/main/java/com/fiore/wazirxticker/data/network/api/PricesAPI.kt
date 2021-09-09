package com.fiore.wazirxticker.data.network.api

import com.fiore.wazirxticker.data.database.entities.Coin
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PricesAPI {

    @GET("api/v2/tickers/{coin}")
    fun getPrice(
        @Path("coin") coinAndCurrency : String
    ) : Call<Coin>

}