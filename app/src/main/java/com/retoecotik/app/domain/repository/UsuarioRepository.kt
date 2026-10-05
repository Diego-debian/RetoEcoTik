package com.retoecotik.app.domain.repository

import com.retoecotik.app.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de persistencia y gestión para el usuario activo (STD-INT-01 / STD-PER-01).
 */
interface UsuarioRepository {
    fun obtenerUsuarioActivo(): Flow<Usuario?>
    suspend fun buscarPorCorreo(correo: String): Usuario?
    suspend fun registrar(nombre: String, correo: String, passwordHash: String, passwordSalt: String): Long
    suspend fun activarSesion(usuarioId: Long)
    suspend fun actualizarBalance(usuarioId: Long, ecopuntosGanados: Int, co2AdicionalKg: Double)
    suspend fun cerrarSesion()
}
