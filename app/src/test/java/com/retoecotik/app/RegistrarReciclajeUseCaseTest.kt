package com.retoecotik.app

import com.retoecotik.app.domain.model.Recompensa
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.domain.model.TipoMaterial
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.ReciclajeRepository
import com.retoecotik.app.domain.repository.RetoRepository
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.usecase.RegistrarReciclajeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para el flujo completo del módulo prioritario de reciclaje (UC-01 / RF08).
 * Valida persistencia en historial, actualización de balance de usuario y progreso de retos.
 * Evidencia: GA7-220501096-AA3-EV02.
 */
class RegistrarReciclajeUseCaseTest {

    private class MockReciclajeRepository : ReciclajeRepository {
        val registros = mutableListOf<RegistroReciclaje>()
        override fun obtenerHistorial(usuarioId: Long): Flow<List<RegistroReciclaje>> = flowOf(registros)
        override suspend fun registrarEntrega(registro: RegistroReciclaje): Long {
            val id = (registros.size + 1).toLong()
            registros.add(registro.copy(id = id))
            return id
        }
        override suspend fun obtenerTotalKilosReciclados(usuarioId: Long): Double = registros.sumOf { it.pesoKg }
        override suspend fun obtenerTotalEcopuntosHistoricos(usuarioId: Long): Int = registros.sumOf { it.ecopuntosGanados }
    }

    private class MockUsuarioRepository : UsuarioRepository {
        var ecopuntos = 100
        var co2 = 10.0
        override fun obtenerUsuarioActivo(): Flow<Usuario?> = flowOf(null)
        override suspend fun buscarPorCorreo(correo: String): Usuario? = null
        override suspend fun registrar(nombre: String, correo: String, passwordHash: String, passwordSalt: String): Long = 1L
        override suspend fun activarSesion(usuarioId: Long) {}
        override suspend fun actualizarBalance(usuarioId: Long, ecopuntosGanados: Int, co2AdicionalKg: Double) {
            ecopuntos += ecopuntosGanados
            co2 += co2AdicionalKg
        }
        override suspend fun cerrarSesion() {}
    }

    private class MockRetoRepository : RetoRepository {
        var progresoAcumulado = 0.0
        override fun obtenerRetosActivos(): Flow<List<Reto>> = flowOf(emptyList())
        override fun obtenerRecompensasDisponibles(): Flow<List<Recompensa>> = flowOf(emptyList())
        override suspend fun inicializarCatalogoInicialSiVacio() {}
        override suspend fun actualizarProgresoRetos(kilosNuevos: Double) {
            progresoAcumulado += kilosNuevos
        }
    }

    private lateinit var reciclajeRepo: MockReciclajeRepository
    private lateinit var usuarioRepo: MockUsuarioRepository
    private lateinit var retoRepo: MockRetoRepository
    private lateinit var useCase: RegistrarReciclajeUseCase

    @Before
    fun setUp() {
        reciclajeRepo = MockReciclajeRepository()
        usuarioRepo = MockUsuarioRepository()
        retoRepo = MockRetoRepository()
        useCase = RegistrarReciclajeUseCase(reciclajeRepo, usuarioRepo, retoRepo)
    }

    @Test
    fun registrarReciclaje_datosValidos_persisteActualizaBalanceYRetos() = runBlocking {
        val registro = useCase(
            usuarioId = 1L,
            tipoMaterial = TipoMaterial.PLASTICO,
            pesoKg = 5.0,
            ubicacion = "Punto Limpio Biblioteca"
        )

        assertNotNull("El registro retornado debe existir", registro)
        assertEquals(1L, registro.id)
        assertEquals(50, registro.ecopuntosGanados) // 5kg * 10 = 50 pts
        assertEquals(7.5, registro.co2EvitadoKg, 0.001) // 5kg * 1.5 = 7.5 kg CO2

        // Verificar persistencia en repositorio de reciclaje
        assertEquals(1, reciclajeRepo.registros.size)
        assertEquals(TipoMaterial.PLASTICO, reciclajeRepo.registros[0].tipoMaterial)

        // Verificar acreditación en balance de usuario
        assertEquals(150, usuarioRepo.ecopuntos) // 100 base + 50 ganados
        assertEquals(17.5, usuarioRepo.co2, 0.001) // 10.0 base + 7.5 ganado

        // Verificar avance en retos
        assertEquals(5.0, retoRepo.progresoAcumulado, 0.001)
    }

    @Test
    fun registrarReciclaje_multiplesEntregas_acumulanCronologicamente() = runBlocking {
        useCase(1L, TipoMaterial.PLASTICO, 2.0)
        useCase(1L, TipoMaterial.VIDRIO, 4.0)
        useCase(1L, TipoMaterial.PAPEL_CARTON, 6.0)

        assertEquals("Deben haberse persistido 3 entregas", 3, reciclajeRepo.registros.size)
        assertEquals(1L, reciclajeRepo.registros[0].id)
        assertEquals(2L, reciclajeRepo.registros[1].id)
        assertEquals(3L, reciclajeRepo.registros[2].id)
        assertTrue("Cada entrega debe registrar una marca de tiempo válida", reciclajeRepo.registros[2].fechaHoraMillis > 0)
        assertEquals(12.0, reciclajeRepo.obtenerTotalKilosReciclados(1L), 0.001)
    }
}
