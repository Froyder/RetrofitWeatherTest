package com.example.retrofitfulltest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.weather_fragment.*

private val REQUEST_CODE = 123
private var GPSPermission = false
private var lastTownRequest = "Omsk"

class WeatherFragment : Fragment() {

    //Ленивая инициализация модели
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            GPSPermission = sharedPref.getBoolean("GPSPermission", false)
            lastTownRequest = sharedPref.getString("LastRequest", lastTownRequest).toString()
        }

        viewModel.getData(lastTownRequest).observe(viewLifecycleOwner, Observer<WeatherData> { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLocationManager()

        searchButton.setOnClickListener() {
            if (cityNameET.text.toString() != ""){
                viewModel.getData(cityNameET.text.toString().trim())
            } else Toast.makeText(context, R.string.inputReminder, Toast.LENGTH_SHORT).show()
        }

        currentCityButton.setOnClickListener() {
            if (!GPSPermission) {
                showRequestDialog()
            } else {
                if (currentCity != "") viewModel.getData(currentCity)
                else Toast.makeText(context, R.string.tryAgainLater, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderData(data: WeatherData) {
        when (data) {
            is WeatherData.Success -> {
                setWeatherData(data)
                loader.visibility = View.GONE
                weatherDataLinear.visibility = View.VISIBLE
            }

            is WeatherData.Loading -> {
                loader.visibility = View.VISIBLE
                weatherDataLinear.visibility = View.GONE
            }
            is WeatherData.Error -> {
                cityTV.setText(R.string.loadingError)
            }
        }
    }

    private fun setWeatherData(data: WeatherData.Success) {
        TransitionManager.beginDelayedTransition(weatherDataLinear, Slide(Gravity.BOTTOM))
        val weatherData = data.serverResponseData
        val name = weatherData.name
        val country = weatherData.sys.country
        val temp = weatherData.main.temp
        val tempFormatted = "%.1f".format(temp)
        val tempFeelsLike = weatherData.main.tempFeelsLike
        val tempFLFormated = "%.1f".format(tempFeelsLike)
        val weatherState = weatherData.weather[0].main
        cityTV.text = "City: $name, $country"
        tempTV.text = "Temperature: $tempFormatted C° (feels like $tempFLFormated C°)"
        weatherTV.text = "Weather: $weatherState"

        val sharedPref = activity?.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString("LastRequest", name)?.apply()
    }

    private fun setLocationManager() {
        activity?.let { context ->
            val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val locationListener: LocationListener = MyLocationListener(activity as AppCompatActivity)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermission()
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 100f, locationListener
        )
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        val sharedPref = activity?.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                        sharedPref?.edit()?.putBoolean("GPSPermission", true)?.apply()
                        activity?.recreate()
                    }
                    return
                } else showRequestDialog()
            }
        }
    }

    private fun showRequestDialog() {
        this.let {
            context?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setTitle(R.string.gpsRequest)
                    .setMessage(R.string.gpsMessage)
                    .setPositiveButton(R.string.gpsAgree) { _, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE
                        )
                    }
                    .setNegativeButton(R.string.gpsDeny) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }
}
