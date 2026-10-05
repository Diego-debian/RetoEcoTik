package com.retoecotik.app.presentation.screens.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.presentation.components.EcoBottomBar
import com.retoecotik.app.presentation.components.EcoButton
import com.retoecotik.app.presentation.components.EcoTopBar
import com.retoecotik.app.presentation.components.ReciclajeItemCard
import com.retoecotik.app.presentation.components.StatCard
import com.retoecotik.app.presentation.navigation.ScreenRoute

/**
 * Pantalla principal y dashboard de impacto ecológico (SCR_03_HOME).
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val usuario = uiState.usuario

    Scaffold(
        topBar = {
            EcoTopBar(
                titulo = "RetoEcoTik",
                ecopuntos = usuario?.ecopuntosTotales ?: 0
            )
        },
        bottomBar = {
            EcoBottomBar(
                currentRoute = ScreenRoute.Home.route,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Saludo y Nivel del Usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Avatar ecológico",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Hola, ${usuario?.nombre ?: "EcoAmigo"}!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Nivel: ${usuario?.nivel ?: "Semilla Verde"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de acción rápida: Registrar Reciclaje (Módulo Prioritario)
            EcoButton(
                text = "+ Registrar Nueva Entrega",
                onClick = { onNavigate(ScreenRoute.Reciclaje) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu Impacto Ambiental Acumulado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Métricas de Impacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    titulo = "Ecopuntos",
                    valor = "${usuario?.ecopuntosTotales ?: 0}",
                    subtitulo = "Disponibles",
                    icon = Icons.Default.Eco,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "CO₂ Evitado",
                    valor = "${usuario?.co2EvitadoKg ?: 0.0} kg",
                    subtitulo = "Estimado",
                    icon = Icons.Default.Co2,
                    colorIcono = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            StatCard(
                titulo = "Total Residuos Aprovechados",
                valor = "${uiState.totalKilosReciclados} kg",
                subtitulo = "Pesaje registrado en puntos ecológicos",
                icon = Icons.Default.Scale,
                colorIcono = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sección de Retos Activos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Retos Destacados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(ScreenRoute.Retos) }) {
                    Text("Ver todos")
                }
            }

            if (uiState.retosActivos.isEmpty()) {
                Text(
                    text = "No hay retos disponibles en este momento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                uiState.retosActivos.forEach { reto ->
                    RetoItemPreview(reto = reto)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Últimas Entregas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimas Entregas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(ScreenRoute.Historial) }) {
                    Text("Ver historial")
                }
            }

            if (uiState.ultimasEntregas.isEmpty()) {
                Text(
                    text = "Aún no has registrado entregas. ¡Empieza hoy!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                uiState.ultimasEntregas.forEach { registro ->
                    ReciclajeItemCard(registro = registro)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RetoItemPreview(reto: Reto) {
    val porcentaje = if (reto.metaKg > 0) (reto.progresoKg / reto.metaKg).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reto.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "+${reto.recompensaPuntos} pts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reto.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { porcentaje },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${reto.progresoKg} kg de ${reto.metaKg} kg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
