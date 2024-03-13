package br.com.fiap.climaapi_app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

    var locateState by remember {
        mutableStateOf("")
    }
    var Mensage by remember {
        mutableStateOf("")
    }
    var dataState by remember {
        mutableStateOf<OpenWeather?>(null)
    }
    var isLoading by remember {
        mutableStateOf(false)
    }

    var color1Search = color1
    var color2Search = color2
    var icone: Painter = painterResource(id = R.drawable.sol)

    if (dataState != null) {
        if (dataState?.weather?.firstOrNull()?.main == "Clear") {
            color1Search = colorResource(id = R.color.azul_claro)
            color2Search = colorResource(id = R.color.azul_escuro)
            icone = painterResource(id = R.drawable.sol)

        } else if (dataState?.weather?.firstOrNull()?.main == "Rain") {
            color1Search = colorResource(id = R.color.cinza)
            color2Search = colorResource(id = R.color.azul_escuro)
            icone = painterResource(id = R.drawable.chuva)

        } else if (dataState?.weather?.firstOrNull()?.main == "Snow") {
            color1Search = colorResource(id = R.color.branco)
            color2Search = colorResource(id = R.color.azul_claro)
            icone = painterResource(id = R.drawable.neve)

        } else if (dataState?.weather?.firstOrNull()?.main == "Clouds") {
            color1Search = colorResource(id = R.color.branco)
            color2Search = colorResource(id = R.color.cinza)
            icone = painterResource(id = R.drawable.nuvens)

        } else if (dataState?.weather?.firstOrNull()?.main == "Haze") {
            color1Search = colorResource(id = R.color.azul_claro)
            color2Search = colorResource(id = R.color.azul_escuro)
            icone = painterResource(id = R.drawable.vento)
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
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "voltar")
                }
                Spacer(modifier = Modifier.width(32.dp))
                OutlinedTextField(
                    value = locateState, onValueChange = { locateState = it },
                    placeholder = {
                        Text(
                            text = "Digite um local"
                        )
                    },
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
                            val call = RetrofitFactory().getOpenWeatherService()
                                .getWeatherByCity(locateState)
                            call.enqueue(object : Callback<OpenWeather> {
                                override fun onResponse(
                                    call: Call<OpenWeather>,
                                    response: Response<OpenWeather>
                                ) {
                                    if (response.isSuccessful) {
                                        dataState = response.body()
                                        isLoading = true
                                    } else {
                                        Mensage =
                                            "Erro, Confira se o local foi digitado corretamente!"
                                    }
                                }

                                override fun onFailure(call: Call<OpenWeather>, t: Throwable) {
                                    Mensage = "Erro, Confira se o local foi digitado corretamente!"
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
                    visible = true, enter = fadeIn(tween(2000)), exit = fadeOut(
                        tween(1000)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            dataState?.weather?.firstOrNull()?.main?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(color = Color.White),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Image(
                                painter = icone,
                                contentDescription = "icone do clima",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Humidade",
                                    style = TextStyle(color = Color.White),
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = dataState?.main?.humidity.toString() + "%",
                                    style = TextStyle(color = Color.White),
                                    fontSize = 24.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ventos",
                                    style = TextStyle(color = Color.White),
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = dataState?.wind?.speed.toString() + "km/h",
                                    style = TextStyle(color = Color.White),
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = Mensage)
                }
            }
        }
    }
}

