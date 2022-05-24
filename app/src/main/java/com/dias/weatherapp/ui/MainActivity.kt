package com.dias.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dias.weatherapp.R
import com.dias.weatherapp.data.response.ListItem
import com.dias.weatherapp.data.response.WeatherResponse
import com.dias.weatherapp.databinding.ActivityMainBinding
import com.dias.weatherapp.utils.LOCATION_PERMISSION_REQ_CODE
import com.dias.weatherapp.utils.iconSizeWeather4x
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupFullScreen()

        // set up query text listener
        setupCityQueryListener()

        // observe weather data
        observeWeather()

        // observe forecast data
        observeForecast()

        // get current location weather
        getCurrentLocationWeather()
    }

    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightNavigationBars = true
    }


    private fun observeForecast() {
        viewModel.getForecast().observe(this) {
            setupView(null, it)
        }
    }

    private fun observeWeather() {
        viewModel.getWeather().observe(this) {
            setupView(it, null)
        }
    }

    private fun setupView(weatherResponse: WeatherResponse?, forecastResponse: List<ListItem>?) {
        weatherResponse?.let {
            binding.tvCity.text = it.name
            binding.tvDegree.text = getString(R.string.temp_format, it.main?.temp)
            val iconUrl =
                "https://openweathermap.org/img/wn/${it.weather?.get(0)?.icon}${iconSizeWeather4x}"
            Glide.with(binding.imgIcWeather).load(iconUrl)
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .into(binding.imgIcWeather)
            setBackgroundBasedOnWeather(it.weather?.get(0)?.id, it.weather?.get(0)?.icon)
        }

        forecastResponse?.let {
            binding.rvForecastWeather.apply {
                adapter = forecastAdapter
                forecastAdapter.setData(it)
            }
        }


    }

    private fun setBackgroundBasedOnWeather(weatherId: Int?, icon: String?) {
        weatherId?.let {
            val background = when (weatherId) {
                in resources.getIntArray(R.array.thunderstorm_id_list) -> R.drawable.thunderstorm
                in resources.getIntArray(R.array.drizzle_id_list) -> R.drawable.drizzle
                in resources.getIntArray(R.array.rain_id_list) -> R.drawable.rain
                in resources.getIntArray(R.array.freezing_rain_id_list) -> R.drawable.freezing_rain
                in resources.getIntArray(R.array.snow_id_list) -> R.drawable.snow
                in resources.getIntArray(R.array.sleet_id_list) -> R.drawable.sleet
                in resources.getIntArray(R.array.clear_id_list) -> if (icon == "01d") R.drawable.clear else R.drawable.clear_night
                in resources.getIntArray(R.array.clouds_id_list) -> R.drawable.lightcloud
                in resources.getIntArray(R.array.heavy_clouds_id_list) -> R.drawable.heavycloud
                in resources.getIntArray(R.array.fog_id_list) -> R.drawable.fog
                in resources.getIntArray(R.array.sand_id_list) -> R.drawable.sand
                in resources.getIntArray(R.array.dust_id_list) -> R.drawable.dust
                in resources.getIntArray(R.array.volcanic_ash_id_list) -> R.drawable.volcanic
                in resources.getIntArray(R.array.squalls_id_list) -> R.drawable.squalls
                in resources.getIntArray(R.array.tornado_id_list) -> R.drawable.tornado
                else -> R.drawable.clear // added for safety
            }
            Glide.with(this).load(background).into(binding.imgBgWeather)
        }
    }

    private fun setupCityQueryListener() {
        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // don't let query be null when accessing network data
                query?.let {
                    viewModel.searchByCity(it)
                    viewModel.forecastByCity(it)
                }

                // hide keyboard
                try {
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error hiding keyboard: ${e.message}")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestLocation() {
        // check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // request location
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.let {
                        val location = it.lastLocation
                        location.let { loc ->
                            Log.d("MainActivity", "Location: $loc")
                            viewModel.weatherByCurrentLocation(loc.latitude, loc.longitude)
                            viewModel.forecastByCurrentLocation(loc.latitude, loc.longitude)
                        }
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun getCurrentLocationWeather() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            // show choose permission dialog
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQ_CODE)
            // and then override onRequestPermissionsResult
            // that function will be called after user choose permission
            // if granted, then requestLocation() will be called
        }
        requestLocation()

    }

}
