package com.retoecotik.app.domain.model

/**
 * Registro de pesaje y entrega de material reciclable (RF08 / UC-01 / Historial: NOT_EXPLICITLY_DEFINED).
 * Representa la acción medular del módulo prioritario de RetoEcoTik.
 */
data class RegistroReciclaje(
    val id: Long = 0,
    val usuarioId: Long,
    val tipoMaterial: TipoMaterial,
    val pesoKg: Double,
    val ecopuntosGanados: Int,
    val co2EvitadoKg: Double,
    val fechaHoraMillis: Long,
    val descripcionUbicacion: String = "Punto Ecológico Central"
)
