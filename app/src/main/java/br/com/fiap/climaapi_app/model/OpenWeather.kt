package br.com.fiap.climaapi_app.model

data class OpenWeather(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
)

data class Weather(
    val main: String,
    val description: String,
)

data class Main(
    val humidity: Int,
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)