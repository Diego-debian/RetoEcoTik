package com.retoecotik.app.domain.usecase

import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.security.PasswordHasher

/**
 * Caso de uso para autenticación y registro local de usuarios (SCR_01_LOGIN / SCR_02_REGISTRO).
 * Implementa derivación de claves mediante PBKDF2WithHmacSHA256 y salt aleatorio por usuario
 * para cumplir con STD-SEC-01 (prohibido almacenar contraseñas en claro).
 */
class AutenticarUsuarioUseCase(
    private val usuarioRepository: UsuarioRepository
) {

    /**
     * Valida las credenciales de inicio de sesión derivando el hash con el salt almacenado.
     */
    suspend fun iniciarSesion(correo: String, passwordPlano: String): Usuario? {
        val correoNormalizado = correo.trim().lowercase()
        val usuario = usuarioRepository.buscarPorCorreo(correoNormalizado) ?: return null

        val passwordValido = PasswordHasher.verificar(
            passwordPlano = passwordPlano,
            saltHex = usuario.passwordSalt,
            expectedHashHex = usuario.passwordHash
        )

        return if (passwordValido) {
            usuarioRepository.activarSesion(usuario.id)
            usuario
        } else {
            null
        }
    }

    /**
     * Registra un nuevo usuario generando un salt aleatorio exclusivo y derivando la clave con PBKDF2.
     */
    suspend fun registrarUsuario(nombre: String, correo: String, passwordPlano: String): Long {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(correo.contains("@") && correo.contains(".")) { "Ingrese un correo electrónico válido" }
        require(passwordPlano.length >= 4) { "La contraseña debe tener al menos 4 caracteres" }

        val correoNormalizado = correo.trim().lowercase()
        val existente = usuarioRepository.buscarPorCorreo(correoNormalizado)
        require(existente == null) { "Ya existe una cuenta con este correo electrónico" }

        val saltBytes = PasswordHasher.generarSalt()
        val hashHex = PasswordHasher.hashear(passwordPlano, saltBytes)
        val saltHex = PasswordHasher.bytesToHex(saltBytes)

        return usuarioRepository.registrar(
            nombre = nombre.trim(),
            correo = correoNormalizado,
            passwordHash = hashHex,
            passwordSalt = saltHex
        )
    }
}
