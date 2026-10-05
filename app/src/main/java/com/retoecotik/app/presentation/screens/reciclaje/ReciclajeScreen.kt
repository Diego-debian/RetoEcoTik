package com.retoecotik.app.presentation.screens.reciclaje

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.retoecotik.app.domain.model.TipoMaterial
import com.retoecotik.app.presentation.components.EcoBottomBar
import com.retoecotik.app.presentation.components.EcoButton
import com.retoecotik.app.presentation.components.EcoTopBar
import com.retoecotik.app.presentation.navigation.ScreenRoute

/**
 * Pantalla de registro de reciclaje y pesaje (SCR_04_RECICLAJE / UC-01).
 * Núcleo del módulo prioritario de software implementado en PHASE_06.
 */
@Composable
fun ReciclajeScreen(
    viewModel: ReciclajeViewModel,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val usuario = uiState.usuario

    Scaffold(
        topBar = {
            EcoTopBar(
                titulo = "Registrar Reciclaje",
                ecopuntos = usuario?.ecopuntosTotales ?: 0
            )
        },
        bottomBar = {
            EcoBottomBar(
                currentRoute = ScreenRoute.Reciclaje.route,
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

            Text(
                text = "1. Selecciona el Tipo de Residuo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Cada material posee un factor ecológico diferente de ecopuntos por kilogramo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Materiales
            TipoMaterial.values().forEach { material ->
                MaterialSelectorItem(
                    material = material,
                    seleccionado = uiState.materialSeleccionado == material,
                    onSelect = { viewModel.onMaterialSeleccionado(material) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "2. Ingresa el Peso en Kilogramos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.pesoInput,
                onValueChange = { viewModel.onPesoChanged(it) },
                label = { Text("Peso en kilogramos (ej. 3.5)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Scale, contentDescription = "Báscula")
                },
                suffix = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "3. Punto Ecológico de Entrega",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.ubicacionSeleccionada,
                onValueChange = { viewModel.onUbicacionChanged(it) },
                label = { Text("Ubicación del contenedor / sede") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Ubicación")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de Vista Previa de Ecopuntos Calculados en Tiempo Real
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Estimación de Recompensa (RF09 / RF10)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "Ecopuntos",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+${uiState.ecopuntosEstimados} Ecopuntos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Co2,
                                contentDescription = "CO2 evitado",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "-${uiState.co2EstimadoKg} kg CO₂",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isProcesando) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                EcoButton(
                    text = "Continuar a Confirmación",
                    onClick = { viewModel.solicitarConfirmacion() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Modal de confirmación canónica (SCR_09_CONFIRMAR_RECICLAJE)
        if (uiState.mostrarDialogoConfirmacion) {
            val pesoValido = uiState.pesoInput.toDoubleOrNull() ?: 0.0
            ConfirmarReciclajeDialog(
                material = uiState.materialSeleccionado,
                pesoKg = pesoValido,
                ecopuntos = uiState.ecopuntosEstimados,
                co2Kg = uiState.co2EstimadoKg,
                ubicacion = uiState.ubicacionSeleccionada,
                onConfirmar = {
                    viewModel.confirmarEntrega { _ ->
                        // Éxito gestionado por el estado
                    }
                },
                onDescartar = { viewModel.descartarConfirmacion() }
            )
        }

        // Diálogo de éxito tras registrar entrega
        uiState.registroCompletado?.let { registro ->
            AlertDialog(
                onDismissRequest = { viewModel.limpiarRegistroCompletado() },
                title = { Text("¡Entrega Registrada con Éxito!") },
                text = {
                    Text(
                        "Has acreditado exitosamente +${registro.ecopuntosGanados} ecopuntos a tu balance. " +
                                "Evitaste aproximadamente ${registro.co2EvitadoKg} kg de emisiones de CO₂."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.limpiarRegistroCompletado()
                            onNavigate(ScreenRoute.Historial)
                        }
                    ) {
                        Text("Ver en Historial")
                    }
                }
            )
        }
    }
}

@Composable
private fun MaterialSelectorItem(
    material: TipoMaterial,
    seleccionado: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(width = if (seleccionado) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = material.nombreLegible,
                    tint = if (seleccionado) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = material.nombreLegible,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = "Factor: ${material.factorPuntosPorKg} pts/kg | ${material.factorCo2KgPorKg} kg CO₂/kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (seleccionado) {
                Text(
                    text = "SELECCIONADO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
