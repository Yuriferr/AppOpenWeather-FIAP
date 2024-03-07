package br.com.fiap.climaapi_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.fiap.climaapi_app.model.OpenWeather
import br.com.fiap.climaapi_app.service.RetrofitFactory
import br.com.fiap.climaapi_app.ui.theme.ClimaAPIAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    ScreenWeather()
                }
            }
        }
    }
}

//18cad22e69252aa4dd95daa8da20c3f3


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWeather() {

    var cityState by remember {
        mutableStateOf("")
    }

    var dataState by remember {
        mutableStateOf<OpenWeather?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = cityState,
                onValueChange = {
                    cityState = it
                },
                modifier = Modifier.weight(2f),
                label = {
                    Text(text = "Nome da cidade")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                )
            )
            IconButton(onClick = {
                val call = RetrofitFactory().getOpenWeatherService().getWeatherByCity(cityState)
                call.enqueue(object : Callback<OpenWeather> {
                    override fun onResponse(
                        call: Call<OpenWeather>,
                        response: Response<OpenWeather>
                    ) {
                        if (response.isSuccessful) {
                            dataState = response.body()
                            Log.d("FIAP", "Response: $dataState")
                        } else {
                            Log.e("FIAP", "Response not successful: ${response.code()} ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<OpenWeather>, t: Throwable) {
                        Log.i("FIAP", "onResponse: ${t.message}")
                    }

                })
            }
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
        }
        dataState?.weather?.firstOrNull()?.let { Text(text = it.main) }
    }
}





