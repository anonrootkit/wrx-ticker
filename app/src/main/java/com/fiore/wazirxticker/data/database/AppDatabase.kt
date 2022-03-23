package com.fiore.wazirxticker.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.fiore.wazirxticker.data.database.converters.Converters
import com.fiore.wazirxticker.data.database.daos.CoinPricesDao
import com.fiore.wazirxticker.data.database.daos.HistoryDao
import com.fiore.wazirxticker.data.database.daos.InvestmentsDao
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.data.database.entities.Investment

@Database(
    entities = [
        Coin::class,
        Investment::class,
        History::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(
    value = [Converters::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinPricesDao(): CoinPricesDao
    abstract fun investmentsDao(): InvestmentsDao
    abstract fun historyDao(): HistoryDao

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