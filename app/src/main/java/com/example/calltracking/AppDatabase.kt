package com.example.calltracking

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calltracking.dataclass.CallLogEntity

@Database(entities = [CallLogEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun callLogDao(): CallLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "call_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}