package br.com.fiap.climaapi_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import br.com.fiap.climaapi_app.model.OpenWeather
import br.com.fiap.climaapi_app.service.RetrofitFactory
import br.com.fiap.climaapi_app.ui.theme.ClimaAPIAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClimaAPIAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Screen()
                }
            }
        }
    }
}

@Composable
fun Screen() {
    var dataState by remember { mutableStateOf<OpenWeather?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun fetchData(city: String) {
        isLoading = true
        val call = RetrofitFactory().getOpenWeatherService().getWeatherByCity(city)
        call.enqueue(object : Callback<OpenWeather> {
            override fun onResponse(
                call: Call<OpenWeather>,
                response: Response<OpenWeather>
            ) {
                if (response.isSuccessful) {
                    dataState = response.body()
                    errorMessage = null
                } else {
                    errorMessage =
                        "Erro na resposta da API: ${response.code()} ${response.message()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<OpenWeather>, t: Throwable) {
                errorMessage = "Falha na chamada da API: ${t.message}"
                isLoading = false
            }
        })
    }

    fun getLocationAndFetchWeather() {
        isLoading = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> =
                        geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as List<Address>
                    val cityName = addresses[0].locality
                    fetchData(cityName)
                } ?: run {
                    isLoading = false
                    errorMessage = "Localização não disponível"
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = "Erro ao obter localização: ${e.message}"
            }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocationAndFetchWeather()
        } else {
            errorMessage = "Permissão de localização negada"
        }
    }

    fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocationAndFetchWeather()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(Unit) {
        checkLocationPermission()
    }

    if (isLoading) {
        // Tela de carregamento
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Tela mostrando as informações da API
        if (errorMessage != null) {
            // Exibir mensagem de erro, se houver
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage!!)
            }
        } else {
            // Exibir dados do clima
            dataState?.let { ScreenWeather(weather = it) }
        }
    }
}


@Composable
fun ScreenWeather(weather: OpenWeather) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .background(Color.Magenta)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { /*TODO*/ }, modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(30.dp), enabled = false
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "invisible")
                }
                weather.weather.firstOrNull()?.description?.let { Text(text = it) }
                IconButton(
                    onClick = { /*TODO*/ }, modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(30.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "pesquisa")
                }
            }
        }
    }
}





