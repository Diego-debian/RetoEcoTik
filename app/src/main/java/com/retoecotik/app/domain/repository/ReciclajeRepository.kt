package com.retoecotik.app.domain.repository

import com.retoecotik.app.domain.model.RegistroReciclaje
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el módulo de reciclaje e historial (UC-01 / STD-PER-01).
 */
interface ReciclajeRepository {
    fun obtenerHistorial(usuarioId: Long): Flow<List<RegistroReciclaje>>
    suspend fun registrarEntrega(registro: RegistroReciclaje): Long
    suspend fun obtenerTotalKilosReciclados(usuarioId: Long): Double
    suspend fun obtenerTotalEcopuntosHistoricos(usuarioId: Long): Int
}
