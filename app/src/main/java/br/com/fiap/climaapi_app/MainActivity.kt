package br.com.fiap.climaapi_app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import br.com.fiap.climaapi_app.model.OpenWeather
import br.com.fiap.climaapi_app.screens.CarregamentoScreen
import br.com.fiap.climaapi_app.screens.ErroScreen
import br.com.fiap.climaapi_app.screens.PesquisaScreen
import br.com.fiap.climaapi_app.screens.PrincipalScreen
import br.com.fiap.climaapi_app.screens.SugestoesScreen
import br.com.fiap.climaapi_app.service.RetrofitFactory
import br.com.fiap.climaapi_app.ui.theme.ClimaAPIAppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Screen() {
    val navController = rememberAnimatedNavController()

    var dataState by remember { mutableStateOf<OpenWeather?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun fetchDataByCoordinates(latitude: Double, longitude: Double) {
        val call =
            RetrofitFactory().getOpenWeatherService().getWeatherByCoordinates(latitude, longitude)
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
                    fetchDataByCoordinates(it.latitude, it.longitude)
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

    var color1: Color = colorResource(id = R.color.azul_claro)
    var color2: Color = colorResource(id = R.color.azul_escuro)
    var icone: Painter = painterResource(id = R.drawable.sol)
    var sugestao: String = ""

    fun isDayTime(): Boolean {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return hour >= 6 && hour < 18 // Assume que o dia começa às 6h e termina às 18h
    }

    val isDay = isDayTime()

    if (dataState?.weather?.firstOrNull()?.main == "Clear") {
        if (isDay) {
            color1 = colorResource(id = R.color.azul_claro)
            color2 = colorResource(id = R.color.azul_escuro)
            icone = painterResource(id = R.drawable.sol)
            sugestao = stringResource(id = R.string.clear_day)

        } else {
            color1 = colorResource(id = R.color.preto)
            color2 = colorResource(id = R.color.roxo)
            icone = painterResource(id = R.drawable.lua)
            sugestao = stringResource(id = R.string.clear_night)

        }
    } else if (dataState?.weather?.firstOrNull()?.main == "Rain") {
        color1 = colorResource(id = R.color.cinza)
        color2 = colorResource(id = R.color.azul_escuro)
        icone = painterResource(id = R.drawable.chuva)
        sugestao = stringResource(id = R.string.rain)

    } else if (dataState?.weather?.firstOrNull()?.main == "Snow") {
        color1 = colorResource(id = R.color.branco)
        color2 = colorResource(id = R.color.azul_claro)
        icone = painterResource(id = R.drawable.neve)
        sugestao = stringResource(id = R.string.snow)

    } else if (dataState?.weather?.firstOrNull()?.main == "Clouds") {
        color1 = colorResource(id = R.color.branco)
        color2 = colorResource(id = R.color.cinza)
        icone = painterResource(id = R.drawable.nuvens)
        sugestao = stringResource(id = R.string.clouds)

    } else if (dataState?.weather?.firstOrNull()?.main == "Haze") {
        color1 = colorResource(id = R.color.azul_claro)
        color2 = colorResource(id = R.color.azul_escuro)
        icone = painterResource(id = R.drawable.vento)
        sugestao = stringResource(id = R.string.haze)
    }

    if (isLoading) {
        // Tela de carregamento
        CarregamentoScreen()
    } else {
        // Tela mostrando as informações da API
        errorMessage?.let {
            // Exibir mensagem de erro, se houver
            ErroScreen(errorMessage = it)
        }
            ?: // Exibir dados do clima
            AnimatedNavHost(
                navController = navController,
                startDestination = "principal",
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentScope.SlideDirection.End,
                        animationSpec = tween(1000)
                    ) + fadeOut(animationSpec = tween(1000))
                },
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Start,
                        animationSpec = tween(500)
                    )
                }
            ) {
                composable(route = "principal") {
                    dataState?.let { it1 ->
                        PrincipalScreen(
                            weather = it1,
                            navController,
                            color1,
                            color2,
                            icone,
                            sugestao,
                        )
                    }
                }
                composable(route = "sugestoes") {
                    SugestoesScreen(navController, color1, color2, icone, sugestao)
                }
                composable(route = "pesquisa") {
                    PesquisaScreen(navController, color1, color2)
                }
            }
    }
}





