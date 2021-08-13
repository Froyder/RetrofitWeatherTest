package com.example.retrofitfulltest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_fragment.*

class WeatherFragment : Fragment() {

    //Ленивая инициализация модели
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData().observe(viewLifecycleOwner, Observer<WeatherData> { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchButton.setOnClickListener() {
            viewModel.getData(cityNameET.text.toString()).observe(viewLifecycleOwner, Observer<WeatherData> { renderData(it) })
        }
    }

    private fun renderData(data: WeatherData) {
        when (data) {
            is WeatherData.Success -> {
                val serverResponseData = data.serverResponseData
                val temp = serverResponseData.main.temp
                val tempFormatted = "%.0f".format(temp)
                val name = serverResponseData.name
                tempTV.text = "City - $name, temperature - $tempFormatted C"
            }

            is WeatherData.Loading -> {
                //Отобразите загрузку
                //showLoading()
            }
            is WeatherData.Error -> {


            }
        }
    }
}
