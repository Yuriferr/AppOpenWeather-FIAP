package br.com.fiap.climaapi_app.screens

import android.content.Context
import android.location.Address
import android.location.Geocoder
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.climaapi_app.model.OpenWeather
import java.io.IOException
import java.util.Locale

@Composable
fun PrincipalScreen(
    weather: OpenWeather,
    navController: NavController,
    color1: Color,
    color2: Color,
    icone: Painter,
    sugestao: String
) {

    val context = LocalContext.current
    var addressText by remember {
        mutableStateOf("")
    }

    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                addressText = address.adminArea // Obter estado
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressText
    }

    LaunchedEffect(Unit) {
        weather.coord.let { getAddressFromLocation(context, it.lat, it.lon) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(color1, color2)))
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
                    onClick = { /*TODO*/ }, modifier = Modifier
                        .background(Color.Transparent, CircleShape)
                        .size(46.dp), enabled = false
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "invisible",
                        tint = Color.Transparent
                    )
                }
                Text(text = addressText, fontSize = 32.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.White))
                IconButton(
                    onClick = {
                              navController.navigate("pesquisa")
                    }, modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(46.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "pesquisa")
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    weather.weather.firstOrNull()?.description?.let {
                        Text(
                            text = it,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = icone,
                        contentDescription = "icone do clima",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = weather.main.temp.toString() + "°C",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = Color.White)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = sugestao.take(100) + "...",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        style = TextStyle(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                                  navController.navigate("sugestoes")
                        },
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        Text(
                            text = "Ver mais",
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}