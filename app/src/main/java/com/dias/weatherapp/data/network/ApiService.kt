package com.dias.weatherapp.data.network

import com.dias.weatherapp.BuildConfig.API_KEY
import com.dias.weatherapp.data.response.ForecastResponse
import com.dias.weatherapp.data.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") appId: String? = API_KEY,
        @Query("units") units: String? = "metric"
    ): Call<WeatherResponse>

    @GET("forecast")
    fun getForecastByCity(
        @Query("q") city: String,
        @Query("appid") appId: String? = API_KEY,
        @Query("units") units: String? = "metric" // return in celcius
    ): Call<ForecastResponse>

    // call this function when app first time open
    @GET("weather")
    fun getWeatherByCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String? = API_KEY,
        @Query("units") units: String? = "metric"
    ): Call<WeatherResponse>

    @GET("forecast")
    fun getForecastByCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String? = API_KEY,
        @Query("units") units: String? = "metric"
    ): Call<ForecastResponse>
}