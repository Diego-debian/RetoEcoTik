package com.retoecotik.app.presentation.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.retoecotik.app.presentation.navigation.ScreenRoute

data class NavigationItem(
    val route: ScreenRoute,
    val icon: ImageVector,
    val label: String
)

/**
 * Barra de navegación inferior principal de RetoEcoTik (Phase 02 / SCREEN_CATALOG.md).
 * Cumple con STD-ACC-01 (altura mínima y zonas de toque accesibles).
 */
@Composable
fun EcoBottomBar(
    currentRoute: String,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem(ScreenRoute.Home, Icons.Default.Home, "Inicio"),
        NavigationItem(ScreenRoute.Reciclaje, Icons.Default.AddCircle, "Reciclar"),
        NavigationItem(ScreenRoute.Historial, Icons.Default.History, "Historial"),
        NavigationItem(ScreenRoute.Retos, Icons.Default.EmojiEvents, "Retos"),
        NavigationItem(ScreenRoute.Perfil, Icons.Default.Person, "Perfil")
    )

    NavigationBar(
        modifier = modifier.heightIn(min = 64.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "Pestaña de navegación: ${item.label}"
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
