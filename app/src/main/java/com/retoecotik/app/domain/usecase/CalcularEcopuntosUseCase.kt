package com.retoecotik.app.domain.usecase

import com.retoecotik.app.domain.model.TipoMaterial
import kotlin.math.ceil
import kotlin.math.round

/**
 * Resultado del cálculo de impacto y recompensa para una entrega de reciclaje.
 */
data class CalculoReciclajeResult(
    val ecopuntos: Int,
    val co2EvitadoKg: Double
)

/**
 * Caso de uso que aplica las reglas de negocio para la conversión de pesaje a ecopuntos (RF08 / UC-01).
 * Aplica factores ambientales proporcionales por cada kilogramo entregado.
 */
class CalcularEcopuntosUseCase {

    operator fun invoke(tipoMaterial: TipoMaterial, pesoKg: Double): CalculoReciclajeResult {
        require(pesoKg > 0.0) { "El peso a registrar debe ser estrictamente mayor a 0 kg" }
        require(pesoKg <= 100.0) { "Por seguridad operativa, el peso máximo por entrega es de 100 kg" }

        val puntosCalculados = ceil(pesoKg * tipoMaterial.factorPuntosPorKg).toInt()
        val co2Calculado = round((pesoKg * tipoMaterial.factorCo2KgPorKg) * 100.0) / 100.0

        return CalculoReciclajeResult(
            ecopuntos = maxOf(1, puntosCalculados),
            co2EvitadoKg = co2Calculado
        )
    }
}
