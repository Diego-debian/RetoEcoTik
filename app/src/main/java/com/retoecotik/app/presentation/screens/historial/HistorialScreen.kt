package com.retoecotik.app.presentation.screens.historial

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.retoecotik.app.presentation.components.EcoBottomBar
import com.retoecotik.app.presentation.components.EcoTopBar
import com.retoecotik.app.presentation.components.ReciclajeItemCard
import com.retoecotik.app.presentation.components.StatCard
import com.retoecotik.app.presentation.navigation.ScreenRoute

/**
 * Pantalla de historial de reciclaje del usuario (SCR_06_HISTORIAL).
 */
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val usuario = uiState.usuario

    Scaffold(
        topBar = {
            EcoTopBar(
                titulo = "Historial de Reciclaje",
                ecopuntos = usuario?.ecopuntosTotales ?: 0
            )
        },
        bottomBar = {
            EcoBottomBar(
                currentRoute = ScreenRoute.Historial.route,
                onNavigate = onNavigate
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Resumen de Entregas Realizadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            titulo = "Total Entregas",
                            valor = "${uiState.registros.size}",
                            subtitulo = "Registradas en Room",
                            icon = Icons.Default.History,
                            colorIcono = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            titulo = "Pesaje Total",
                            valor = "${uiState.totalKilos} kg",
                            subtitulo = "Acumulado",
                            icon = Icons.Default.Scale,
                            colorIcono = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Listado Cronológico",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (uiState.registros.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no tienes registros de entrega.\nRealiza tu primer pesaje en la pestaña Reciclar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(uiState.registros, key = { it.id }) { registro ->
                        ReciclajeItemCard(registro = registro)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
