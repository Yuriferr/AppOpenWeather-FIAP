package br.com.fiap.climaapi_app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.climaapi_app.R
import br.com.fiap.climaapi_app.model.OpenWeather
import br.com.fiap.climaapi_app.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesquisaScreen(navController: NavController, color1: Color, color2: Color) {
    var locateState by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var dataState by remember { mutableStateOf<OpenWeather?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var color1Search by remember { mutableStateOf(color1) }
    var color2Search by remember { mutableStateOf(color2) }
    var icon: Painter = painterResource(id = R.drawable.sol)

    if (dataState != null) {
        when (dataState?.weather?.firstOrNull()?.main) {
            "Clear" -> {
                color1Search = colorResource(id = R.color.azul_claro)
                color2Search = colorResource(id = R.color.azul_escuro)
                icon = painterResource(id = R.drawable.sol)
            }
            "Rain" -> {
                color1Search = colorResource(id = R.color.cinza)
                color2Search = colorResource(id = R.color.azul_escuro)
                icon = painterResource(id = R.drawable.chuva)
            }
            "Snow" -> {
                color1Search = colorResource(id = R.color.branco)
                color2Search = colorResource(id = R.color.azul_claro)
                icon = painterResource(id = R.drawable.neve)
            }
            "Clouds" -> {
                color1Search = colorResource(id = R.color.branco)
                color2Search = colorResource(id = R.color.cinza)
                icon = painterResource(id = R.drawable.nuvens)
            }
            "Haze" -> {
                color1Search = colorResource(id = R.color.azul_claro)
                color2Search = colorResource(id = R.color.azul_escuro)
                icon = painterResource(id = R.drawable.vento)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(color1Search, color2Search)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "voltar")
                }
                Spacer(modifier = Modifier.width(32.dp))
                OutlinedTextField(
                    value = locateState,
                    onValueChange = { locateState = it },
                    placeholder = { Text("Digite um local") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    shape = CircleShape,
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.White,
                        focusedBorderColor = Color.White
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            val call = RetrofitFactory().getOpenWeatherService().getWeatherByCity(locateState)
                            call.enqueue(object : Callback<OpenWeather> {
                                override fun onResponse(call: Call<OpenWeather>, response: Response<OpenWeather>) {
                                    if (response.isSuccessful) {
                                        dataState = response.body()
                                        isLoading = true
                                    } else {
                                        message = "Erro, Confira se o local foi digitado corretamente!"
                                    }
                                }

                                override fun onFailure(call: Call<OpenWeather>, t: Throwable) {
                                    message = "Erro, Confira se o local foi digitado corretamente!"
                                }
                            })
                        }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "pesquisa",
                                tint = Color.White
                            )
                        }
                    }
                )
            }

            if (isLoading) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(2000)),
                    exit = fadeOut(tween(1000))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        dataState?.weather?.firstOrNull()?.description?.let { weather ->
                            val capitalizedWeather = weather.capitalize()
                            Text(
                                text = capitalizedWeather,
                                style = TextStyle(color = Color.White),
                                fontSize = 36.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Image(
                            painter = icon,
                            contentDescription = "icone do clima",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                InfoText(text = "Humidade")
                                InfoText(text = dataState?.main?.humidity.toString() + "%")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                InfoText(text = "Ventos")
                                InfoText(text = dataState?.wind?.speed.toString() + "km/h")
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = message)
                }
            }
        }
    }
}

@Composable
fun InfoText(text: String) {
    Text(text = text, style = TextStyle(color = Color.White), fontSize = 24.sp)
}
