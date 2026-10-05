package com.retoecotik.app.data.repository

import com.retoecotik.app.data.local.dao.UsuarioDao
import com.retoecotik.app.data.local.entity.UsuarioEntity
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de usuario con Room (STD-INT-01 / STD-PER-01).
 */
class UsuarioRepositoryImpl(
    private val usuarioDao: UsuarioDao
) : UsuarioRepository {

    override fun obtenerUsuarioActivo(): Flow<Usuario?> {
        return usuarioDao.obtenerUsuarioActivoFlow().map { it?.toDomain() }
    }

    override suspend fun buscarPorCorreo(correo: String): Usuario? {
        return usuarioDao.buscarPorCorreo(correo.trim().lowercase())?.toDomain()
    }

    override suspend fun registrar(
        nombre: String,
        correo: String,
        passwordHash: String,
        passwordSalt: String
    ): Long {
        usuarioDao.cerrarTodasLasSesiones()
        val nuevo = UsuarioEntity(
            nombre = nombre,
            correo = correo.trim().lowercase(),
            passwordHash = passwordHash,
            passwordSalt = passwordSalt,
            ecopuntosTotales = 50, // Bono de bienvenida inicial
            nivel = "Semilla Verde",
            co2EvitadoKg = 0.0,
            esSesionActiva = true
        )
        return usuarioDao.insertar(nuevo)
    }

    override suspend fun activarSesion(usuarioId: Long) {
        usuarioDao.cerrarTodasLasSesiones()
        usuarioDao.activarSesion(usuarioId)
    }

    override suspend fun actualizarBalance(
        usuarioId: Long,
        ecopuntosGanados: Int,
        co2AdicionalKg: Double
    ) {
        usuarioDao.incrementarBalance(usuarioId, ecopuntosGanados, co2AdicionalKg)
    }

    override suspend fun cerrarSesion() {
        usuarioDao.cerrarTodasLasSesiones()
    }
}
