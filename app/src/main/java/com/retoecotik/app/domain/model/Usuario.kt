package com.retoecotik.app.domain.model

/**
 * Representación inmutable del perfil de usuario en el dominio de RetoEcoTik (RF01, RF02).
 * Contiene el balance de ecopuntos acumulados y la estimación de CO₂ evitado.
 */
data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val correo: String,
    val passwordHash: String = "",
    val passwordSalt: String = "",
    val ecopuntosTotales: Int = 0,
    val nivel: String = "Semilla Verde",
    val co2EvitadoKg: Double = 0.0
)
