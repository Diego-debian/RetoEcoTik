package com.retoecotik.app

import com.retoecotik.app.domain.model.TipoMaterial
import com.retoecotik.app.domain.usecase.CalcularEcopuntosUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para las reglas de negocio de cálculo de ecopuntos y CO₂ evitado (RF08 / UC-01).
 * Evidencia: GA7-220501096-AA3-EV02.
 */
class CalcularEcopuntosUseCaseTest {

    private lateinit var useCase: CalcularEcopuntosUseCase

    @Before
    fun setUp() {
        useCase = CalcularEcopuntosUseCase()
    }

    @Test
    fun calcularEcopuntos_plasticoPesoEntero_calculaExacto() {
        val resultado = useCase(TipoMaterial.PLASTICO, 5.0)
        assertEquals(50, resultado.ecopuntos)
        assertEquals(7.5, resultado.co2EvitadoKg, 0.001)
    }

    @Test
    fun calcularEcopuntos_metalPesoDecimal_calculaConRedondeo() {
        val resultado = useCase(TipoMaterial.METAL, 2.5)
        assertEquals(38, resultado.ecopuntos) // ceil(2.5 * 15) = ceil(37.5) = 38
        assertEquals(5.0, resultado.co2EvitadoKg, 0.001) // 2.5 * 2.0 = 5.0
    }

    @Test
    fun calcularEcopuntos_todosLosMateriales_calculanFactoresCorrectos() {
        // Vidrio: factor 8 pts, 0.3 CO2
        val vidrio = useCase(TipoMaterial.VIDRIO, 10.0)
        assertEquals(80, vidrio.ecopuntos)
        assertEquals(3.0, vidrio.co2EvitadoKg, 0.001)

        // Papel/Cartón: factor 6 pts, 0.9 CO2
        val papel = useCase(TipoMaterial.PAPEL_CARTON, 10.0)
        assertEquals(60, papel.ecopuntos)
        assertEquals(9.0, papel.co2EvitadoKg, 0.001)

        // Orgánico: factor 4 pts, 0.5 CO2
        val organico = useCase(TipoMaterial.ORGANICO, 10.0)
        assertEquals(40, organico.ecopuntos)
        assertEquals(5.0, organico.co2EvitadoKg, 0.001)
    }

    @Test
    fun calcularEcopuntos_pesoDecimalMuyPequeno_garantizaMinimoUnEcopunto() {
        val resultado = useCase(TipoMaterial.PAPEL_CARTON, 0.05)
        assertTrue("Incluso con pesos mínimos debe acreditar al menos 1 ecopunto", resultado.ecopuntos >= 1)
        assertTrue("El CO2 evitado debe ser positivo", resultado.co2EvitadoKg >= 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calcularEcopuntos_pesoCero_lanzaExcepcion() {
        useCase(TipoMaterial.PLASTICO, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calcularEcopuntos_pesoNegativo_lanzaExcepcion() {
        useCase(TipoMaterial.VIDRIO, -3.5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calcularEcopuntos_pesoMayorACienKg_lanzaExcepcion() {
        useCase(TipoMaterial.METAL, 100.5)
    }
}
