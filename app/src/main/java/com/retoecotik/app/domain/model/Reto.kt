package com.retoecotik.app.domain.model

/**
 * Reto de gamificación ambiental propuesto a la comunidad (RF11, RF12).
 */
data class Reto(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val metaKg: Double,
    val progresoKg: Double = 0.0,
    val recompensaPuntos: Int,
    val completado: Boolean = false
)
