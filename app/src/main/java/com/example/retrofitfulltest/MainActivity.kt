package com.example.retrofitfulltest

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, WeatherFragment()).commit()
    }
}