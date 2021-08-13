package com.example.retrofitfulltest

sealed class WeatherData {
    data class Success(val serverResponseData: WeatherResponseData) : WeatherData()
    data class Error(val error: Throwable) : WeatherData()
    data class Loading(val progress: Int?) : WeatherData()
}
