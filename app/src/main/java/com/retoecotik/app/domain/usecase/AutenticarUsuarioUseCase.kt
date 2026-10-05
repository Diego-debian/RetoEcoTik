package com.retoecotik.app.domain.usecase

import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import java.security.MessageDigest

/**
 * Caso de uso para autenticación y registro local de usuarios (SCR_01_LOGIN / SCR_02_REGISTRO).
 * Implementa hashing SHA-256 para cumplir con STD-SEC-01 (prohibido almacenar contraseñas en claro).
 */
class AutenticarUsuarioUseCase(
    private val usuarioRepository: UsuarioRepository
) {

    fun hashearPassword(passwordPlano: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(passwordPlano.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun iniciarSesion(correo: String, passwordPlano: String): Usuario? {
        val hash = hashearPassword(passwordPlano)
        return usuarioRepository.autenticar(correo.trim().lowercase(), hash)
    }

    suspend fun registrarUsuario(nombre: String, correo: String, passwordPlano: String): Long {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(correo.contains("@") && correo.contains(".")) { "Ingrese un correo electrónico válido" }
        require(passwordPlano.length >= 4) { "La contraseña debe tener al menos 4 caracteres" }

        val hash = hashearPassword(passwordPlano)
        return usuarioRepository.registrar(nombre.trim(), correo.trim().lowercase(), hash)
    }
}
