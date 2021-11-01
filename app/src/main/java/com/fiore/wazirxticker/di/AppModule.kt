package com.fiore.wazirxticker.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.fiore.wazirxticker.data.database.AppDatabase
import com.fiore.wazirxticker.data.network.api.PricesAPI
import com.fiore.wazirxticker.data.network.config.APIService
import com.fiore.wazirxticker.utils.PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun providePricesAPI(apiService: APIService): PricesAPI {
        return apiService.createService(PricesAPI::class.java)
    }

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}