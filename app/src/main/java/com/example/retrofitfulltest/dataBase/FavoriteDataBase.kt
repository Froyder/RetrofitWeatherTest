package com.example.retrofitfulltest.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(City::class), version = 1)
abstract class FavoriteDataBase : RoomDatabase() {
    abstract fun cityDAO(): CityDAO
}