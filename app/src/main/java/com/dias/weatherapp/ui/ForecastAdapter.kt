package com.dias.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dias.weatherapp.BuildConfig
import com.dias.weatherapp.R
import com.dias.weatherapp.data.response.ListItem
import com.dias.weatherapp.databinding.RowItemWeatherBinding
import com.dias.weatherapp.utils.iconSizeWeather2x
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val forecastList = ArrayList<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder =
        ForecastViewHolder(
            RowItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = forecastList.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.binding.apply {
            val forecast = forecastList[position]
            // parse date from date string
            val date =
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                    Locale.getDefault()).parse(forecast.dtTxt.toString()) as Date
            val context = tvMinDegree.context
            tvItemDate.text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date)
            tvItemTime.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            tvMinDegree.text =
                context.getString(R.string.min_degree, forecast.main?.tempMin)
            tvMaxDegree.text =
                context.getString(R.string.max_degree, forecast.main?.tempMax)
            // gliding image

            val iconUrl =
                "https://openweathermap.org/img/wn/${forecast.weather?.get(0)?.icon}$iconSizeWeather2x"
            Glide.with(imgItemWeather).load(iconUrl)
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .into(imgItemWeather)
        }
    }

    class ForecastViewHolder(val binding: RowItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setData(list: List<ListItem>?) {
        forecastList.clear()
        list?.let { forecastList.addAll(it) }
    }


}
