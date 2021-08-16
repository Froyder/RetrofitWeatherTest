package com.example.retrofitfulltest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response

class WeatherViewModel(
    private val liveDataForViewToObserve: MutableLiveData<WeatherData> = MutableLiveData(),
    private val retrofitImpl: WeatherRetrofitImplementation = WeatherRetrofitImplementation()
) :
    ViewModel() {

    fun getData(city : String? = "Omsk"): LiveData<WeatherData> {
        sendServerRequest(city)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(city :String?) {
        liveDataForViewToObserve.value = WeatherData.Loading(null)
        val apiKey: String = BuildConfig.WEATHER_API_KEY
        if (apiKey.isBlank()) {
            WeatherData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getWeatherFromServer(city, apiKey).enqueue(object :
                retrofit2.Callback<WeatherResponseData> {

                override fun onResponse(
                    call: Call<WeatherResponseData>,
                    response: Response<WeatherResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            WeatherData.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                WeatherData.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                WeatherData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponseData>, t: Throwable) {
                    liveDataForViewToObserve.value = WeatherData.Error(t)
                }
            })
        }
    }
}