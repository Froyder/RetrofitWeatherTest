package com.example.retrofitfulltest.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.retrofitfulltest.R
import com.example.retrofitfulltest.TimeZoneManager
import com.example.retrofitfulltest.WeatherResponseData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.weather_fragment.*
import kotlinx.android.synthetic.main.weather_fragment.cityTV
import kotlinx.android.synthetic.main.weather_fragment.tempTV

class MyBottomSheetDialogFragment(weather: WeatherResponseData) : BottomSheetDialogFragment() {

    val weatherData = weather

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog, weatherContainer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = weatherData.name
        val country = weatherData.sys.country
        val temp = weatherData.main.temp
        val tempFormatted = "%.1f".format(temp)
        val tempFeelsLike = weatherData.main.tempFeelsLike
        val tempFLFormated = "%.1f".format(tempFeelsLike)
        val weatherState = weatherData.weather[0].description
        val tempMin = weatherData.main.tempMin
        val tempMinF = "%.1f".format(tempMin)
        val tempMax = weatherData.main.tempMax
        val tempMaxF = "%.1f".format(tempMax)
        val wind = weatherData.wind.speed
        val localTime = TimeZoneManager.getLocalTime(weatherData.coord.lat, weatherData.coord.lon)

        cityTV.text = "City: $name, $country"
        tempTV.text = "Temperature: $tempFormatted C° (feels like $tempFLFormated C°)"
        tempTVminMax.text = "Min: $tempMinF C°, max: $tempMaxF C°"
        windTV.text = "Wind speed: $wind m/s"
        weatherTV.text = "Weather: $weatherState"
        timeTV.text = localTime
    }

    companion object {
        const val TAG = "Bottom Sheet Dialog Fragment"
    }
}