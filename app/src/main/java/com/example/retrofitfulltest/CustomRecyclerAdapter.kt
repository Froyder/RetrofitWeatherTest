package com.example.retrofitfulltest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitfulltest.dataBase.City

class CustomRecyclerAdapter(
    private val cities: MutableList<City>,
) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>(), ItemTouchHelperAdapter {

    //колбэк от внутреннего интерфейса для реализации листенеров
    var onCitySelectedCallback: CitySelectedCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.favorite_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val cityName = cities[position].name
        viewHolder.cityTV?.text = cityName

        //регистрируем лисенеры
        viewHolder.cardView?.setOnClickListener {
            onCitySelectedCallback?.onCitySelected(city = cities[position])
        }

        viewHolder.cityTV?.setOnClickListener {
            onCitySelectedCallback?.onCitySelected(city = cities[position])
        }

        viewHolder.deleteIV?.setOnClickListener(){
            onCitySelectedCallback?.onCityDelete(city = cities[position])
        }

        //слушатель долгого нажатия
        viewHolder.cityTV?.setOnLongClickListener(){
            onCitySelectedCallback?.onCityLongSelected(city = cities[position])
            true
        }

        viewHolder.cardView?.setOnLongClickListener(){
            onCitySelectedCallback?.onCityLongSelected(city = cities[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    //метод отнаследованного интерфейса на передвижение элемента
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        cities.removeAt(fromPosition).apply {
            cities.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    //метод отнаследованного интерфейса на смахивание элемента
    override fun onItemDismiss(position: Int) {
        onCitySelectedCallback?.onCityDelete(city = cities[position])
    }

    //интерфейс для обработки нажатий
    interface CitySelectedCallback {
        fun onCitySelected(city: City)
        fun onCityDelete(city: City)
        fun onCityLongSelected(city: City)
    }

    //ВЬЮХОЛДЕР
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        var cityTV: TextView? = null
        var cardView : CardView? = null
        var deleteIV : ImageView? = null

        init {
            cityTV = itemView.findViewById(R.id.cityTV)
            cardView = itemView.findViewById(R.id.favoriteCardView)
            deleteIV = itemView.findViewById(R.id.deleteIV)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }
}

//интерфейс для обработки движения и смахивания элементов
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}

//интерфейс для обработки элементов со стороны холдера
interface ItemTouchHelperViewHolder {

    fun onItemSelected()

    fun onItemClear()
}