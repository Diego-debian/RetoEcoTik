package com.retoecotik.app.presentation.screens.retos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.retoecotik.app.domain.model.Recompensa
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.presentation.components.EcoBottomBar
import com.retoecotik.app.presentation.components.EcoTopBar
import com.retoecotik.app.presentation.navigation.ScreenRoute

/**
 * Pantalla de retos ecológicos y catálogo de recompensas (SCR_05_RETOS / SCR_07_RECOMPENSAS).
 */
@Composable
fun RetosScreen(
    viewModel: RetosViewModel,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val usuario = uiState.usuario

    Scaffold(
        topBar = {
            EcoTopBar(
                titulo = if (selectedTab == 0) "Retos Ecológicos" else "Recompensas",
                ecopuntos = usuario?.ecopuntosTotales ?: 0
            )
        },
        bottomBar = {
            EcoBottomBar(
                currentRoute = ScreenRoute.Retos.route,
                onNavigate = onNavigate
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Retos Activos (${uiState.retos.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Recompensas (${uiState.recompensas.size})") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (selectedTab == 0) {
                    items(uiState.retos, key = { it.id }) { reto ->
                        RetoCardItem(reto = reto)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    items(uiState.recompensas, key = { it.id }) { recompensa ->
                        RecompensaCardItem(
                            recompensa = recompensa,
                            puntosUsuario = usuario?.ecopuntosTotales ?: 0,
                            onCanjear = { viewModel.canjearRecompensa(recompensa) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        uiState.mensajeCanje?.let { mensaje ->
            AlertDialog(
                onDismissRequest = { viewModel.limpiarMensaje() },
                title = { Text("Canje de Recompensa") },
                text = { Text(mensaje) },
                confirmButton = {
                    Button(onClick = { viewModel.limpiarMensaje() }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@Composable
private fun RetoCardItem(reto: Reto) {
    val progreso = if (reto.metaKg > 0) (reto.progresoKg / reto.metaKg).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reto.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+${reto.recompensaPuntos} pts",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reto.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (reto.completado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${reto.progresoKg} kg alcanzados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = if (reto.completado) "¡COMPLETADO!" else "Meta: ${reto.metaKg} kg",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (reto.completado) FontWeight.Bold else FontWeight.Normal,
                    color = if (reto.completado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun RecompensaCardItem(
    recompensa: Recompensa,
    puntosUsuario: Int,
    onCanjear: () -> Unit
) {
    val puedeCanjear = puntosUsuario >= recompensa.costoEcopuntos

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recompensa.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${recompensa.costoEcopuntos} pts",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recompensa.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Categoría: ${recompensa.categoria} | Stock: ${recompensa.stockDisponible}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCanjear,
                enabled = puedeCanjear,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (puedeCanjear) "Canjear Recompensa" else "Puntos Insuficientes")
            }
        }
    }
}
