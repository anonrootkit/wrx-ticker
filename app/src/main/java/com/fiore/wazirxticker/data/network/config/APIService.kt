package com.fiore.wazirxticker.data.network.config

import android.content.Context
import com.fiore.wazirxticker.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class APIService @Inject constructor(
    @ApplicationContext context: Context
) {
//    private val collector: ChuckerCollector by lazy {
//        ChuckerCollector(
//            context = context,
//            showNotification = true,
//            retentionPeriod = RetentionManager.Period.ONE_HOUR
//        )
//    }

//    private val chuckerInterceptor: ChuckerInterceptor by lazy {
//        ChuckerInterceptor.Builder(context).apply {
//            collector(collector)
//            maxContentLength(120000L)
//        }.build()
//    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder().apply {
        val TIMEOUT_MILLIS = 20000L // 20 seconds

//        if (BuildConfig.DEBUG) {
//            addNetworkInterceptor(chuckerInterceptor)
//        }

        connectTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        readTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        writeTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        addNetworkInterceptor { chain ->
            val request: Request = chain.request()
            chain.proceed(request)
        }
    }.build()

    private val gson: Gson =
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS000'Z'").create()

    private val retrofitBuilder: Retrofit.Builder =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))

    fun <S> createService(service: Class<S>): S {
        val client = getOkHttpClient()
        return retrofitBuilder.client(client).build().create(service)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val httpClientBuilder: OkHttpClient.Builder = httpClient.newBuilder()
        httpClientBuilder.addInterceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestBuilder: Request.Builder = originalRequest.newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)

            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        return httpClientBuilder.build()
    }
}
