package br.com.fiap.climaapi_app.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    private val URL = "https://api.openweathermap.org/data/2.5/"

    private val retrofitFactory = Retrofit
        .Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getOpenWeatherService(): OpenWeatherService {
        return retrofitFactory.create(OpenWeatherService::class.java)
    }

}