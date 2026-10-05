package com.retoecotik.app.data.repository

import com.retoecotik.app.data.local.dao.RecompensaDao
import com.retoecotik.app.data.local.dao.RetoDao
import com.retoecotik.app.domain.model.Recompensa
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.domain.repository.RetoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de retos y recompensas con Room (STD-INT-01 / STD-PER-01).
 */
class RetoRepositoryImpl(
    private val retoDao: RetoDao,
    private val recompensaDao: RecompensaDao
) : RetoRepository {

    override fun obtenerRetosActivos(): Flow<List<Reto>> {
        return retoDao.obtenerTodosLosRetosFlow().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override fun obtenerRecompensasDisponibles(): Flow<List<Recompensa>> {
        return recompensaDao.obtenerRecompensasDisponiblesFlow().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun inicializarCatalogoInicialSiVacio() {
        // La inicialización se orquesta mediante el Callback de Room en AppDatabase
    }

    override suspend fun actualizarProgresoRetos(kilosNuevos: Double) {
        val retosActivos = retoDao.obtenerRetosActivos()
        for (reto in retosActivos) {
            val nuevoProgreso = reto.progresoKg + kilosNuevos
            val completado = nuevoProgreso >= reto.metaKg
            retoDao.actualizarReto(
                reto.copy(
                    progresoKg = nuevoProgreso,
                    completado = completado
                )
            )
        }
    }
}
