package com.example.retrofitfulltest.dataBase

import androidx.room.Delete

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CityDAO {
    @Insert
    fun insertAll(vararg city: City)

    @Insert
    fun addCity (city: City)

    @Query("SELECT * FROM city WHERE name LIKE :name")
    fun findByName(name: String): City

    @Delete
    fun deleteCity(city: City)

    @Query("SELECT * FROM city")
    fun getAll(): List<City>

    @Query("SELECT * FROM city")
    fun searchCity(): List<City>
}
