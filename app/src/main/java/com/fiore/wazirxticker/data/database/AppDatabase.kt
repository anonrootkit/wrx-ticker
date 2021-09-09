package com.fiore.wazirxticker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fiore.wazirxticker.data.database.converters.Converters
import com.fiore.wazirxticker.data.database.daos.CoinPricesDao
import com.fiore.wazirxticker.data.database.daos.InvestmentsDao
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.Investment

@Database(
    entities = [
        Coin::class,
        Investment::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [Converters::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinPricesDao(): CoinPricesDao
    abstract fun investmentsDao(): InvestmentsDao

    companion object {
        private const val DATABASE_NAME = "ticker-db"

        fun getInstance(context: Context): AppDatabase {
            return buildDatabase(context)
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}