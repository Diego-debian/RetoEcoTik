package com.retoecotik.app

import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.security.PasswordHasher
import com.retoecotik.app.domain.usecase.AutenticarUsuarioUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias de validación para registro y login local (RF01, RF02 / HU01, HU02).
 * Cubre validaciones de formato de texto, longitudes, duplicidad y caracteres especiales.
 * Evidencia: GA7-220501096-AA3-EV02.
 */
class AutenticarUsuarioValidationTest {

    private class MockUsuarioRepository : UsuarioRepository {
        val db = mutableMapOf<String, Usuario>()
        var activeId: Long? = null

        override fun obtenerUsuarioActivo(): Flow<Usuario?> = flowOf(db.values.find { it.id == activeId })

        override suspend fun buscarPorCorreo(correo: String): Usuario? = db[correo.lowercase()]

        override suspend fun registrar(
            nombre: String,
            correo: String,
            passwordHash: String,
            passwordSalt: String
        ): Long {
            val id = (db.size + 1).toLong()
            val u = Usuario(
                id = id,
                nombre = nombre,
                correo = correo.lowercase(),
                passwordHash = passwordHash,
                passwordSalt = passwordSalt
            )
            db[correo.lowercase()] = u
            activeId = id
            return id
        }

        override suspend fun activarSesion(usuarioId: Long) {
            activeId = usuarioId
        }

        override suspend fun actualizarBalance(usuarioId: Long, ecopuntosGanados: Int, co2AdicionalKg: Double) {}

        override suspend fun cerrarSesion() {
            activeId = null
        }
    }

    private lateinit var repository: MockUsuarioRepository
    private lateinit var useCase: AutenticarUsuarioUseCase

    @Before
    fun setUp() {
        repository = MockUsuarioRepository()
        useCase = AutenticarUsuarioUseCase(repository)
    }

    @Test
    fun registro_datosValidosConCaracteresEspeciales_registraExitosamente() = runBlocking {
        val id = useCase.registrarUsuario(
            nombre = "María José Peña-García",
            correo = "maria.pena@ecotik.co",
            passwordPlano = "Cl@ve.Segura#2026*"
        )
        assertEquals(1L, id)
        val user = repository.buscarPorCorreo("maria.pena@ecotik.co")
        assertNotNull(user)
        assertEquals("María José Peña-García", user!!.nombre)
    }

    @Test(expected = IllegalArgumentException::class)
    fun registro_nombreVacio_lanzaExcepcion() = runBlocking {
        useCase.registrarUsuario(
            nombre = "   ",
            correo = "test@ecotik.com",
            passwordPlano = "12345"
        )
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun registro_correoSinArroba_lanzaExcepcion() = runBlocking {
        useCase.registrarUsuario(
            nombre = "Usuario Test",
            correo = "correosinformato.com",
            passwordPlano = "12345"
        )
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun registro_correoSinPunto_lanzaExcepcion() = runBlocking {
        useCase.registrarUsuario(
            nombre = "Usuario Test",
            correo = "usuario@dominio",
            passwordPlano = "12345"
        )
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun registro_passwordMenorACuatroCaracteres_lanzaExcepcion() = runBlocking {
        useCase.registrarUsuario(
            nombre = "Usuario Test",
            correo = "usuario@ecotik.com",
            passwordPlano = "123"
        )
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun registro_correoDuplicado_lanzaExcepcion() = runBlocking {
        useCase.registrarUsuario("Primero", "cuenta@ecotik.com", "pass1234")
        // Intento con el mismo correo (en mayúsculas para probar normalización case-insensitive)
        useCase.registrarUsuario("Segundo", "CUENTA@ECOTIK.COM", "pass5678")
        Unit
    }

    @Test
    fun login_usuarioInexistente_retornaNull() = runBlocking {
        val result = useCase.iniciarSesion("noexiste@ecotik.com", "pass1234")
        assertNull("Login con usuario inexistente debe ser nulo", result)
    }

    @Test
    fun login_claveErronea_retornaNull() = runBlocking {
        useCase.registrarUsuario("Carlos", "carlos@ecotik.com", "Correcta123")
        val result = useCase.iniciarSesion("carlos@ecotik.com", "Incorrecta999")
        assertNull("Login con contraseña incorrecta debe ser rechazado", result)
    }
}
