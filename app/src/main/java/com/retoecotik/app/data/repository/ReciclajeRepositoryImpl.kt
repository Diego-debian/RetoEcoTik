package com.retoecotik.app.data.repository

import com.retoecotik.app.data.local.dao.RegistroReciclajeDao
import com.retoecotik.app.data.local.entity.RegistroReciclajeEntity
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.repository.ReciclajeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de reciclaje con Room (STD-INT-01 / STD-PER-01).
 */
class ReciclajeRepositoryImpl(
    private val registroReciclajeDao: RegistroReciclajeDao
) : ReciclajeRepository {

    override fun obtenerHistorial(usuarioId: Long): Flow<List<RegistroReciclaje>> {
        return registroReciclajeDao.obtenerHistorialPorUsuario(usuarioId).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun registrarEntrega(registro: RegistroReciclaje): Long {
        val entity = RegistroReciclajeEntity.fromDomain(registro)
        return registroReciclajeDao.insertar(entity)
    }

    override suspend fun obtenerTotalKilosReciclados(usuarioId: Long): Double {
        return registroReciclajeDao.sumarKilosPorUsuario(usuarioId)
    }

    override suspend fun obtenerTotalEcopuntosHistoricos(usuarioId: Long): Int {
        return registroReciclajeDao.sumarPuntosPorUsuario(usuarioId)
    }
}
