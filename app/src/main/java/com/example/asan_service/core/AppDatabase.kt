package com.example.asan_service.core

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.asan_service.dao.*
import com.example.asan_service.entity.*

@Database(entities = [WatchItemEntity::class, AccXEntity::class
                     ,AccYEntity::class,
                     AccZEntity::class,
                     GyroXEntity::class,
                     GyroYEntity::class,
                     GyroZEntity::class,
                     BaroEntity::class,
                     LightEntity::class,
                     HeartRateEntity::class,
                     NickNameEntity::class], version = 11)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchItemDao(): WatchItemDao
    abstract fun accXDao() : AccXDao
    abstract fun accYDao() : AccYDao
    abstract fun accZDao() : AccZDao
    abstract fun gyroXDao() : GyroXDao
    abstract fun gyroYDao() : GyroYDao
    abstract fun gyroZDao() : GyroZDao
    abstract fun baroDao() : BaroDao
    abstract fun heartRateDao() : HeartRateDao
    abstract fun lightDao() : LightDao
    abstract fun nickNameDao() : NickNameDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "asanDB"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}