package com.example.retrofitfulltest

import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu

import kotlinx.android.synthetic.main.weather_fragment.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener(){

        }

        setSupportActionBar(bottom_app_bar)
        supportFragmentManager.beginTransaction().replace(R.id.container, WeatherFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }
}