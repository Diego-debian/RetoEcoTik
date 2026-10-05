package com.retoecotik.app

import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.security.PasswordHasher
import com.retoecotik.app.domain.usecase.AutenticarUsuarioUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke test técnico para verificación de autenticación local con PBKDF2WithHmacSHA256 (STD-SEC-01).
 * Verifica los criterios A, B, C y D solicitados por la supervisión de PHASE_06:
 * A. Registrar usuario con derivación de clave.
 * B. Confirmar que la contraseña NUNCA se almacena en texto claro.
 * C. Login exitoso con contraseña correcta.
 * D. Rechazo de autenticación con contraseña incorrecta.
 */
class PasswordAuthSmokeTest {

    // Repositorio en memoria para el smoke test
    private class FakeUsuarioRepository : UsuarioRepository {
        private val usuarios = mutableListOf<Usuario>()
        private var usuarioActivoId: Long? = null

        override fun obtenerUsuarioActivo(): Flow<Usuario?> {
            val activo = usuarios.find { it.id == usuarioActivoId }
            return flowOf(activo)
        }

        override suspend fun buscarPorCorreo(correo: String): Usuario? {
            return usuarios.find { it.correo.equals(correo, ignoreCase = true) }
        }

        override suspend fun registrar(
            nombre: String,
            correo: String,
            passwordHash: String,
            passwordSalt: String
        ): Long {
            val nuevoId = (usuarios.size + 1).toLong()
            val nuevo = Usuario(
                id = nuevoId,
                nombre = nombre,
                correo = correo,
                passwordHash = passwordHash,
                passwordSalt = passwordSalt,
                ecopuntosTotales = 50,
                nivel = "Semilla Verde"
            )
            usuarios.add(nuevo)
            usuarioActivoId = nuevoId
            return nuevoId
        }

        override suspend fun activarSesion(usuarioId: Long) {
            usuarioActivoId = usuarioId
        }

        override suspend fun actualizarBalance(
            usuarioId: Long,
            ecopuntosGanados: Int,
            co2AdicionalKg: Double
        ) {
            val index = usuarios.indexOfFirst { it.id == usuarioId }
            if (index != -1) {
                val u = usuarios[index]
                usuarios[index] = u.copy(
                    ecopuntosTotales = u.ecopuntosTotales + ecopuntosGanados,
                    co2EvitadoKg = u.co2EvitadoKg + co2AdicionalKg
                )
            }
        }

        override suspend fun cerrarSesion() {
            usuarioActivoId = null
        }
    }

    @Test
    fun autenticacion_flujoCompleto_cumpleRequisitosDeSeguridad() = runBlocking {
        val repository = FakeUsuarioRepository()
        val useCase = AutenticarUsuarioUseCase(repository)

        val passwordOriginal = "EcoPass2026*"
        val correo = "reciclador@retoecotik.com"

        // A. Registrar usuario
        val userId = useCase.registrarUsuario(
            nombre = "Carlos Verde",
            correo = correo,
            passwordPlano = passwordOriginal
        )
        assertTrue("El usuario debe registrarse con ID válido", userId > 0)

        // B. Confirmar que la contraseña NUNCA se almacena en texto claro
        val usuarioGuardado = repository.buscarPorCorreo(correo)
        assertNotNull("El usuario debe existir en persistencia", usuarioGuardado)
        assertNotEquals("La contraseña NO debe estar en texto claro", passwordOriginal, usuarioGuardado!!.passwordHash)
        assertNotEquals("El salt NO debe coincidir con la contraseña", passwordOriginal, usuarioGuardado.passwordSalt)
        assertEquals("El hash PBKDF2-256 debe tener 64 caracteres hexadecimales (256 bits)", 64, usuarioGuardado.passwordHash.length)
        assertEquals("El salt debe tener 32 caracteres hexadecimales (16 bytes)", 32, usuarioGuardado.passwordSalt.length)

        // C. Login con contraseña correcta (éxito)
        val loginCorrecto = useCase.iniciarSesion(correo, passwordOriginal)
        assertNotNull("El login con contraseña correcta debe ser exitoso", loginCorrecto)
        assertEquals("El usuario autenticado debe coincidir", "Carlos Verde", loginCorrecto!!.nombre)

        // D. Rechazo con contraseña incorrecta (fallo controlado)
        val loginErroneo = useCase.iniciarSesion(correo, "PasswordIncorrecta999")
        assertNull("El login con contraseña incorrecta debe ser rechazado", loginErroneo)

        // E. Verificación de aleatoriedad del Salt (dos usuarios con la misma contraseña deben tener hashes diferentes)
        val correo2 = "segundo@retoecotik.com"
        useCase.registrarUsuario(
            nombre = "Segundo Usuario",
            correo = correo2,
            passwordPlano = passwordOriginal
        )
        val usuario2 = repository.buscarPorCorreo(correo2)
        assertNotNull(usuario2)
        assertNotEquals("Salts de dos usuarios distintos deben ser diferentes", usuarioGuardado.passwordSalt, usuario2!!.passwordSalt)
        assertNotEquals("Hashes de dos usuarios con misma clave deben ser diferentes gracias al salt aleatorio", usuarioGuardado.passwordHash, usuario2.passwordHash)
    }

    @Test
    fun passwordHasher_verificacionDirecta_validaCorrectamente() {
        val pass = "ClaveSecreta123"
        val salt = PasswordHasher.generarSalt()
        val saltHex = PasswordHasher.bytesToHex(salt)
        val hashHex = PasswordHasher.hashear(pass, salt)

        assertTrue(PasswordHasher.verificar(pass, saltHex, hashHex))
        assertFalse(PasswordHasher.verificar("ClaveIncorrecta", saltHex, hashHex))
        assertFalse(PasswordHasher.verificar("", saltHex, hashHex))
    }
}
