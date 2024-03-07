package br.com.fiap.climaapi_app.service

import br.com.fiap.climaapi_app.model.OpenWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    //    https://api.openweathermap.org/data/2.5/weather?q=Canada&units=metric&appid=18cad22e69252aa4dd95daa8da20c3f3&lang=pt_br
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "18cad22e69252aa4dd95daa8da20c3f3",
        @Query("lang") language: String = "pt_br"
    ): Call<OpenWeather>

}