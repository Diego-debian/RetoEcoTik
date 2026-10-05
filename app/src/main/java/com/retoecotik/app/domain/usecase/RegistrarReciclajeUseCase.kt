package com.retoecotik.app.domain.usecase

import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.TipoMaterial
import com.retoecotik.app.domain.repository.ReciclajeRepository
import com.retoecotik.app.domain.repository.RetoRepository
import com.retoecotik.app.domain.repository.UsuarioRepository

/**
 * Caso de uso medular del MVP (UC-01 / RF08, RF09, RF10).
 * Orquesta el registro de entrega de reciclaje, el cálculo de ecopuntos, la persistencia,
 * la actualización del balance del usuario y el avance de los retos comunitarios activos.
 */
class RegistrarReciclajeUseCase(
    private val reciclajeRepository: ReciclajeRepository,
    private val usuarioRepository: UsuarioRepository,
    private val retoRepository: RetoRepository,
    private val calcularEcopuntosUseCase: CalcularEcopuntosUseCase = CalcularEcopuntosUseCase()
) {

    suspend operator fun invoke(
        usuarioId: Long,
        tipoMaterial: TipoMaterial,
        pesoKg: Double,
        ubicacion: String = "Punto Ecológico Central"
    ): RegistroReciclaje {
        val calculo = calcularEcopuntosUseCase(tipoMaterial, pesoKg)

        val nuevoRegistro = RegistroReciclaje(
            usuarioId = usuarioId,
            tipoMaterial = tipoMaterial,
            pesoKg = pesoKg,
            ecopuntosGanados = calculo.ecopuntos,
            co2EvitadoKg = calculo.co2EvitadoKg,
            fechaHoraMillis = System.currentTimeMillis(),
            descripcionUbicacion = ubicacion
        )

        // 1. Guardar entrega en el repositorio local (Room)
        val idGenerado = reciclajeRepository.registrarEntrega(nuevoRegistro)
        val registroPersistido = nuevoRegistro.copy(id = idGenerado)

        // 2. Acreditar ecopuntos y CO₂ al usuario
        usuarioRepository.actualizarBalance(
            usuarioId = usuarioId,
            ecopuntosGanados = calculo.ecopuntos,
            co2AdicionalKg = calculo.co2EvitadoKg
        )

        // 3. Notificar avance a los retos ecológicos activos
        retoRepository.actualizarProgresoRetos(pesoKg)

        return registroPersistido
    }
}
