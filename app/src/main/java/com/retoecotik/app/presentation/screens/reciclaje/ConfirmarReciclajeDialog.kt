package com.retoecotik.app.presentation.screens.reciclaje

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.retoecotik.app.domain.model.TipoMaterial

/**
 * Diálogo modal de confirmación de entrega de reciclaje (SCR_09_CONFIRMAR_RECICLAJE).
 * Presenta el desglose del pesaje, ecopuntos y huella de carbono antes de persistir la transacción.
 */
@Composable
fun ConfirmarReciclajeDialog(
    material: TipoMaterial,
    pesoKg: Double,
    ecopuntos: Int,
    co2Kg: Double,
    ubicacion: String,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDescartar,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Confirmación de reciclaje",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Confirmar Entrega",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Por favor verifica los datos de tu entrega antes de registrar los ecopuntos:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                DetalleFila(etiqueta = "Material:", valor = material.nombreLegible)
                DetalleFila(etiqueta = "Peso registrado:", valor = "$pesoKg kg")
                DetalleFila(etiqueta = "Punto de entrega:", valor = ubicacion)
                DetalleFila(
                    etiqueta = "Ecopuntos a ganar:",
                    valor = "+$ecopuntos pts",
                    esDestacado = true
                )
                DetalleFila(
                    etiqueta = "Impacto estimado:",
                    valor = "-$co2Kg kg CO₂",
                    esDestacado = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Confirmar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDescartar,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Corregir")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DetalleFila(
    etiqueta: String,
    valor: String,
    esDestacado: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = valor,
            style = if (esDestacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (esDestacado) FontWeight.Bold else FontWeight.Medium,
            color = if (esDestacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
