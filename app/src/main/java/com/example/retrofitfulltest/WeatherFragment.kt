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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.weather_fragment.*

private val REQUEST_CODE = 123
private var GPSPermission = false
private var lastTownRequest = "Omsk"

class WeatherFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by lazy {
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
        weatherViewModel.getData(lastTownRequest).observe(viewLifecycleOwner, Observer<WeatherData> { renderData(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLocationManager()

        searchButton.setOnClickListener() {
            if (cityNameET.text.toString() != ""){
                weatherViewModel.getData(cityNameET.text.toString().trim())
                hideKeyboard ()
            } else Snackbar.make(weatherContainer, R.string.inputReminder, Snackbar.LENGTH_SHORT).show()
        }

        currentCityButton.setOnClickListener() {
            if (!GPSPermission) {
                showRequestDialog()
            } else {
                if (currentCity != "") weatherViewModel.getData(currentCity)
                else Toast.makeText(context, R.string.tryAgainLater, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard () {
        val keyboard =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(cityNameET.getWindowToken(), 0)
    }

    private fun renderData(data: WeatherData) {
        when (data) {
            is WeatherData.Success -> {
                setWeatherData(data)
                loader.visibility = View.GONE
                cardViewLinear.visibility = View.VISIBLE
            }

            is WeatherData.Loading -> {
                loader.visibility = View.VISIBLE
                cardViewLinear.visibility = View.GONE
            }
            is WeatherData.Error -> {
                cityTV.setText(R.string.loadingError)
            }
        }
    }

    private fun setWeatherData(data: WeatherData.Success) {
        TransitionManager.beginDelayedTransition(cardViewLinear, Slide(Gravity.BOTTOM))
        val weatherData = data.serverResponseData
        val name = weatherData.name
        val country = weatherData.sys.country
        val temp = weatherData.main.temp
        val tempFormatted = "%.1f".format(temp)

        cityTV.text = "$name, $country"
        tempTV.text = "$tempFormatted C°"

        when (weatherData.weather[0].main) {
            "Clouds" -> weatherIV.setImageResource(R.drawable.ic_baseline_cloud_24)
            "Clear" -> weatherIV.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
            "Rain" -> weatherIV.setImageResource(R.drawable.ic_baseline_water_drop_24)
            "Drizzle" -> weatherIV.setImageResource(R.drawable.ic_baseline_water_drop_24)
            "Thunderstorm" -> weatherIV.setImageResource(R.drawable.ic_baseline_water_drop_24)
            "Snow" -> weatherIV.setImageResource(R.drawable.ic_baseline_snow)
            else -> weatherIV.setImageResource(R.drawable.ic_baseline_dehaze_24)
        }

        //val localTime = TimeZoneManager.getLocalTime(weatherData.coord.lat, weatherData.coord.lon)

        saveLastRequest(name)

        weatherDataLinear.setOnClickListener(){
            MyBottomSheetDialogFragment(weatherData).show(parentFragmentManager, MyBottomSheetDialogFragment.TAG)
        }
    }

    private fun saveLastRequest (city :String) {
        val sharedPref = activity?.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString("LastRequest", city)?.apply()
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
