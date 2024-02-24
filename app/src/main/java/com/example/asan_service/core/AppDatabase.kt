package com.example.asan_service.core

import androidx.room.Database
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
                     HeartRateEntity::class], version = 2)
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
}