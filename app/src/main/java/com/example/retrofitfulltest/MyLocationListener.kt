package com.example.retrofitfulltest

import android.content.ContentValues
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import java.io.IOException
import java.util.*

var currentCity : String? = ""

class MyLocationListener(mainActivity: AppCompatActivity) : LocationListener {

    private val activity = mainActivity
    var cityName: String? = null

    override fun onLocationChanged(loc: Location) {
        val longitude = loc.getLongitude().toString()
        val latitude = loc.getLatitude().toString()

        /*------- To get city name from coordinates -------- */
        val gcd = Geocoder(activity, Locale.getDefault())
        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
                loc.getLatitude(),
                loc.getLongitude(), 1
            )
            if (addresses.size > 0) {
                System.out.println(addresses[0].getLocality())
                cityName = addresses[0].getLocality()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
//        val s = ("Your current city is: $cityName")
//        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
        currentCity = cityName
    }
}