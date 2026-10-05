package com.retoecotik.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.retoecotik.app.presentation.screens.auth.AuthViewModel
import com.retoecotik.app.presentation.screens.auth.LoginScreen
import com.retoecotik.app.presentation.screens.auth.RegistroScreen
import com.retoecotik.app.presentation.screens.historial.HistorialScreen
import com.retoecotik.app.presentation.screens.historial.HistorialViewModel
import com.retoecotik.app.presentation.screens.home.HomeScreen
import com.retoecotik.app.presentation.screens.home.HomeViewModel
import com.retoecotik.app.presentation.screens.perfil.PerfilScreen
import com.retoecotik.app.presentation.screens.reciclaje.ReciclajeScreen
import com.retoecotik.app.presentation.screens.reciclaje.ReciclajeViewModel
import com.retoecotik.app.presentation.screens.retos.RetosScreen
import com.retoecotik.app.presentation.screens.retos.RetosViewModel

/**
 * Grafo de navegación central de RetoEcoTik (Phase 02 / Phase 06).
 * Articula las 9 pantallas del MVP sin acoplamiento rígido.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    reciclajeViewModel: ReciclajeViewModel,
    historialViewModel: HistorialViewModel,
    retosViewModel: RetosViewModel,
    startDestination: String = ScreenRoute.Home.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ScreenRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo(ScreenRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegistro = {
                    navController.navigate(ScreenRoute.Registro.route)
                }
            )
        }

        composable(ScreenRoute.Registro.route) {
            RegistroScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo(ScreenRoute.Registro.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(ScreenRoute.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { route ->
                    navController.navigate(route.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(ScreenRoute.Reciclaje.route) {
            ReciclajeScreen(
                viewModel = reciclajeViewModel,
                onNavigate = { route ->
                    navController.navigate(route.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(ScreenRoute.Historial.route) {
            HistorialScreen(
                viewModel = historialViewModel,
                onNavigate = { route ->
                    navController.navigate(route.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(ScreenRoute.Retos.route) {
            RetosScreen(
                viewModel = retosViewModel,
                onNavigate = { route ->
                    navController.navigate(route.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(ScreenRoute.Perfil.route) {
            PerfilScreen(
                authViewModel = authViewModel,
                onNavigate = { route ->
                    if (route == ScreenRoute.Login) {
                        navController.navigate(ScreenRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
