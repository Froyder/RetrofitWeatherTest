package com.example.retrofitfulltest.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitfulltest.CustomRecyclerAdapter
import com.example.retrofitfulltest.ItemTouchHelperCallback
import com.example.retrofitfulltest.R
import com.example.retrofitfulltest.dataBase.City
import com.example.retrofitfulltest.dataBase.FavoriteDataBase

class FavoriteFragment(private val dataBase: FavoriteDataBase) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var cities : List<City> = listOf()

        val dbThread = Thread {
            val cityDao = dataBase.cityDAO()
            cities = cityDao.getAll()
        }
        dbThread.start()
        dbThread.join()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = CustomRecyclerAdapter(cities as MutableList<City>)
        recyclerView.adapter = adapter

        //интерфейс для реализации перетягиваний
        ItemTouchHelper(ItemTouchHelperCallback(adapter)).attachToRecyclerView(recyclerView)

        //колбек для обработки нажатий
        adapter.onCitySelectedCallback =
            object : CustomRecyclerAdapter.CitySelectedCallback {
                override fun onCitySelected(city: City) {

                    val sharedPref = context?.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                    sharedPref?.edit()?.putString("LastRequest", city.name)?.apply()

                    parentFragmentManager.beginTransaction().replace(R.id.container, WeatherFragment())
                        .commit()
                }

                override fun onCityDelete(city: City) {
                    val dbThread = Thread {
                        val cityDao = dataBase.cityDAO()
                        cityDao.deleteCity(city)
                        cities = cityDao.getAll()
                    }
                    dbThread.start()
                    dbThread.join()

                    parentFragmentManager.beginTransaction().replace(R.id.container, FavoriteFragment(dataBase))
                        .commit()

                    val cityName = city.name
                    Toast.makeText(context, "$cityName deleted from Favorites", Toast.LENGTH_SHORT).show()
                }

                override fun onCityLongSelected(city: City) {
                }
            }
    }
}