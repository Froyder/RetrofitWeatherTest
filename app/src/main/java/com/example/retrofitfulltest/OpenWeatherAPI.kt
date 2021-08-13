package com.example.retrofitfulltest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPI {

    @GET("/data/2.5/weather?units=metric")
    fun getWeatherFromServer(@Query("q") city: String?, @Query("appid") apiKey: String): Call<WeatherResponseData>

}