package com.dias.weatherapp.data.response

import com.google.gson.annotations.SerializedName

/**
 * Coord is retrieved from android location service
 */

data class WeatherResponse(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("weather")
    val weather: List<WeatherItem>? = null,

    @field:SerializedName("main")
    val main: Main? = null,

    )

data class Main(

    @field:SerializedName("temp_min")
    val tempMin: Double? = null,

    @field:SerializedName("temp")
    val temp: Double? = null,

    @field:SerializedName("temp_max")
    val tempMax: Double? = null,
)