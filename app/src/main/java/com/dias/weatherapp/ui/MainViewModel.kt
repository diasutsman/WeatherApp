package com.dias.weatherapp.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dias.weatherapp.data.network.ApiConfig
import com.dias.weatherapp.data.response.ForecastResponse
import com.dias.weatherapp.data.response.ListItem
import com.dias.weatherapp.data.response.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val weather = MutableLiveData<WeatherResponse>()

    private val forecast = MutableLiveData<List<ListItem>>()

    fun searchByCity(city: String) {
        ApiConfig().getApiService().getWeatherByCity(city).enqueue(
            object : Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("FailureCall", t.message.toString())
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>,
                ) {
                    if (response.isSuccessful) {
                        weather.postValue(response.body())
                    }
                }

            }
        )
    }

    fun forecastByCity(city: String) {
        ApiConfig().getApiService().getForecastByCity(city).enqueue(
            object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>,
                ) {
                    if (response.isSuccessful) {
                        forecast.postValue(response.body()?.list as List<ListItem>)
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Log.e("Failure Forecast Call", t.message.toString())
                }


            }
        )
    }

    fun weatherByCurrentLocation(lat: Double, lon: Double) {
        ApiConfig().getApiService().getWeatherByCurrentLocation(lat, lon).enqueue(
            object : Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("FailureCall", t.message.toString())
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>,
                ) {
                    if (response.isSuccessful) {
                        weather.postValue(response.body())
                    }
                }

            }
        )
    }

    fun forecastByCurrentLocation(lat: Double, lon: Double) {
        ApiConfig().getApiService().getForecastByCurrentLocation(lat, lon).enqueue(
            object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>,
                ) {
                    if (response.isSuccessful) {
                        forecast.postValue(response.body()?.list as List<ListItem>)
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Log.e("Failure Forecast Call", t.message.toString())
                }


            }
        )
    }

    fun getWeather(): MutableLiveData<WeatherResponse> = weather
    fun getForecast(): MutableLiveData<List<ListItem>> = forecast
}