package com.retoecotik.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = SuperficieBlanco,
    primaryContainer = VerdeClaroFondo,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdeSecundario,
    onSecondary = TextoPrincipal,
    tertiary = AmbarAlerta,
    background = GrisFondo,
    onBackground = TextoPrincipal,
    surface = SuperficieBlanco,
    onSurface = TextoPrincipal,
    error = ErrorRojo,
    onError = SuperficieBlanco,
    outline = BordeGris
)

@Composable
fun RetoEcoTikTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Para el MVP mantenemos un esquema luminoso y accesible adaptado a DUA
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
