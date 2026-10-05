package com.retoecotik.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.retoecotik.app.RetoEcoTikApp
import com.retoecotik.app.presentation.navigation.NavGraph
import com.retoecotik.app.presentation.navigation.ScreenRoute
import com.retoecotik.app.presentation.screens.auth.AuthViewModel
import com.retoecotik.app.presentation.screens.historial.HistorialViewModel
import com.retoecotik.app.presentation.screens.home.HomeViewModel
import com.retoecotik.app.presentation.screens.reciclaje.ReciclajeViewModel
import com.retoecotik.app.presentation.screens.retos.RetosViewModel
import com.retoecotik.app.presentation.theme.RetoEcoTikTheme

/**
 * Actividad principal del cliente móvil RetoEcoTik (GA7-220501096-AA3-EV01).
 * Inicializa el grafo de navegación y los ViewModels enlazados a la base de datos Room.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as RetoEcoTikApp

        // Instanciación directa de ViewModels apoyada en el contenedor singleton de RetoEcoTikApp
        val authViewModel = AuthViewModel(
            autenticarUsuarioUseCase = app.autenticarUsuarioUseCase,
            usuarioRepository = app.usuarioRepository
        )

        val homeViewModel = HomeViewModel(
            usuarioRepository = app.usuarioRepository,
            reciclajeRepository = app.reciclajeRepository,
            retoRepository = app.retoRepository
        )

        val reciclajeViewModel = ReciclajeViewModel(
            registrarReciclajeUseCase = app.registrarReciclajeUseCase,
            calcularEcopuntosUseCase = app.calcularEcopuntosUseCase,
            usuarioRepository = app.usuarioRepository
        )

        val historialViewModel = HistorialViewModel(
            usuarioRepository = app.usuarioRepository,
            reciclajeRepository = app.reciclajeRepository
        )

        val retosViewModel = RetosViewModel(
            retoRepository = app.retoRepository,
            usuarioRepository = app.usuarioRepository
        )

        setContent {
            RetoEcoTikTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        reciclajeViewModel = reciclajeViewModel,
                        historialViewModel = historialViewModel,
                        retosViewModel = retosViewModel,
                        startDestination = ScreenRoute.Home.route
                    )
                }
            }
        }
    }
}
