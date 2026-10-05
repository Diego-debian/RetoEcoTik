package com.retoecotik.app.presentation.navigation

/**
 * Rutas canónicas de navegación del cliente móvil RetoEcoTik.
 * Mapeadas explícitamente al catálogo de pantallas SCREEN_CATALOG.md de PHASE_02 (SCR_01 a SCR_09).
 */
sealed class ScreenRoute(
    val route: String,
    val screenId: String,
    val title: String
) {
    object Login : ScreenRoute("login", "SCR_01_LOGIN", "Iniciar Sesión")
    object Registro : ScreenRoute("registro", "SCR_02_REGISTRO", "Crear Cuenta")
    object Home : ScreenRoute("home", "SCR_03_HOME", "Inicio")
    object Reciclaje : ScreenRoute("reciclaje", "SCR_04_RECICLAJE", "Registrar Reciclaje")
    object Retos : ScreenRoute("retos", "SCR_05_RETOS", "Retos Ecológicos")
    object Historial : ScreenRoute("historial", "SCR_06_HISTORIAL", "Historial de Reciclaje")
    object Recompensas : ScreenRoute("recompensas", "SCR_07_RECOMPENSAS", "Recompensas")
    object Perfil : ScreenRoute("perfil", "SCR_08_PERFIL", "Mi Perfil")
    object ConfirmarReciclaje : ScreenRoute("confirmar_reciclaje", "SCR_09_CONFIRMAR_RECICLAJE", "Confirmar Entrega")
}
