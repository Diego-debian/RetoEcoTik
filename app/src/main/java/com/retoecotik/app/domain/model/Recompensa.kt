package com.retoecotik.app.domain.model

/**
 * Beneficio o incentivo canjeable mediante ecopuntos acumulados (RF13, RF14).
 */
data class Recompensa(
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val costoEcopuntos: Int,
    val stockDisponible: Int = 10,
    val categoria: String = "Sostenibilidad"
)
