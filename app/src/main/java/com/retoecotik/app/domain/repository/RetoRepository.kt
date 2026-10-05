package com.retoecotik.app.domain.repository

import com.retoecotik.app.domain.model.Recompensa
import com.retoecotik.app.domain.model.Reto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el catálogo de retos y recompensas de gamificación (RF11 a RF14).
 */
interface RetoRepository {
    fun obtenerRetosActivos(): Flow<List<Reto>>
    fun obtenerRecompensasDisponibles(): Flow<List<Recompensa>>
    suspend fun inicializarCatalogoInicialSiVacio()
    suspend fun actualizarProgresoRetos(kilosNuevos: Double)
}
