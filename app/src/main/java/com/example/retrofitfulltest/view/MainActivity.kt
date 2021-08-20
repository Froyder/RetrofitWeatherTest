package com.example.retrofitfulltest.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.widget.Toast
import androidx.room.Room
import com.example.retrofitfulltest.R
import com.example.retrofitfulltest.TimeZoneManager
import com.example.retrofitfulltest.dataBase.FavoriteDataBase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataBase = Room.databaseBuilder(this,
            FavoriteDataBase::class.java, "CityList").build()

        setButtonsAndView(dataBase)

        val thread = Thread {
            val timeZoneManager = TimeZoneManager
        }
        thread.start()

        //supportFragmentManager.beginTransaction().replace(R.id.container, WeatherFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

        private fun setButtonsAndView(dataBase: FavoriteDataBase) {
        bottom_navigation_view.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.app_bar_weather -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, WeatherFragment(dataBase))
                        .commitAllowingStateLoss()
                    true
                }
                R.id.app_bar_favorite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, FavoriteFragment(dataBase))
                        .commitAllowingStateLoss()
                    true
                }
                R.id.app_bar_history -> {
                    Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        bottom_navigation_view.selectedItemId = R.id.app_bar_weather
    }
}