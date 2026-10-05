package com.retoecotik.app.domain.model

/**
 * Categorías de residuos sólidos aprovechables admitidos por RetoEcoTik (RF08 / UC-01).
 * Incluye los factores oficiales de conversión a ecopuntos e impacto ambiental en kg de CO₂ evitado.
 */
enum class TipoMaterial(
    val nombreLegible: String,
    val factorPuntosPorKg: Int,
    val factorCo2KgPorKg: Double
) {
    PLASTICO("Plástico PET/PEAD", 10, 1.5),
    VIDRIO("Vidrio", 8, 0.3),
    PAPEL_CARTON("Papel y Cartón", 6, 0.9),
    METAL("Metal / Aluminio", 15, 2.0),
    ORGANICO("Orgánico Compostable", 4, 0.5)
}
