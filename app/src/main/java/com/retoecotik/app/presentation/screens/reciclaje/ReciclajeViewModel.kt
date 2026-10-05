package com.retoecotik.app.presentation.screens.reciclaje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.TipoMaterial
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.usecase.CalcularEcopuntosUseCase
import com.retoecotik.app.domain.usecase.RegistrarReciclajeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReciclajeUiState(
    val usuario: Usuario? = null,
    val materialSeleccionado: TipoMaterial = TipoMaterial.PLASTICO,
    val pesoInput: String = "2.5",
    val ubicacionSeleccionada: String = "Punto Ecológico Central",
    val ecopuntosEstimados: Int = 25,
    val co2EstimadoKg: Double = 3.75,
    val mostrarDialogoConfirmacion: Boolean = false,
    val isProcesando: Boolean = false,
    val registroCompletado: RegistroReciclaje? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel del módulo prioritario de registro de reciclaje (UC-01 / RF08).
 * Vinculado a SCR_04_RECICLAJE y modal de confirmación SCR_09_CONFIRMAR_RECICLAJE.
 */
class ReciclajeViewModel(
    private val registrarReciclajeUseCase: RegistrarReciclajeUseCase,
    private val calcularEcopuntosUseCase: CalcularEcopuntosUseCase,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReciclajeUiState())
    val uiState: StateFlow<ReciclajeUiState> = _uiState.asStateFlow()

    init {
        observarUsuario()
        recalcularEstimaciones(_uiState.value.materialSeleccionado, _uiState.value.pesoInput)
    }

    private fun observarUsuario() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { usuario ->
                _uiState.update { it.copy(usuario = usuario) }
            }
        }
    }

    fun onMaterialSeleccionado(nuevoMaterial: TipoMaterial) {
        _uiState.update { it.copy(materialSeleccionado = nuevoMaterial, errorMessage = null) }
        recalcularEstimaciones(nuevoMaterial, _uiState.value.pesoInput)
    }

    fun onPesoChanged(nuevoPeso: String) {
        // Filtrar solo números y punto decimal
        val filtrado = nuevoPeso.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(pesoInput = filtrado, errorMessage = null) }
        recalcularEstimaciones(_uiState.value.materialSeleccionado, filtrado)
    }

    fun onUbicacionChanged(nuevaUbicacion: String) {
        _uiState.update { it.copy(ubicacionSeleccionada = nuevaUbicacion) }
    }

    private fun recalcularEstimaciones(material: TipoMaterial, pesoTexto: String) {
        val peso = pesoTexto.toDoubleOrNull()
        if (peso != null && peso > 0) {
            try {
                val resultado = calcularEcopuntosUseCase(material, peso)
                _uiState.update {
                    it.copy(
                        ecopuntosEstimados = resultado.ecopuntos,
                        co2EstimadoKg = resultado.co2EvitadoKg
                    )
                }
            } catch (_: Exception) {
                // Validación pasiva en edición
            }
        } else {
            _uiState.update { it.copy(ecopuntosEstimados = 0, co2EstimadoKg = 0.0) }
        }
    }

    fun solicitarConfirmacion() {
        val peso = _uiState.value.pesoInput.toDoubleOrNull()
        if (peso == null || peso <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Ingrese un peso válido mayor a 0 kg") }
            return
        }
        if (peso > 100.0) {
            _uiState.update { it.copy(errorMessage = "El peso máximo por entrega es de 100 kg") }
            return
        }
        _uiState.update { it.copy(mostrarDialogoConfirmacion = true, errorMessage = null) }
    }

    fun descartarConfirmacion() {
        _uiState.update { it.copy(mostrarDialogoConfirmacion = false) }
    }

    fun confirmarEntrega(onSuccess: (RegistroReciclaje) -> Unit) {
        val currentState = _uiState.value
        val usuarioActual = currentState.usuario
        val peso = currentState.pesoInput.toDoubleOrNull() ?: 0.0

        if (usuarioActual == null) {
            _uiState.update { it.copy(errorMessage = "Debe haber una sesión activa para registrar reciclaje") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcesando = true,
                    mostrarDialogoConfirmacion = false,
                    errorMessage = null
                )
            }
            try {
                val registro = registrarReciclajeUseCase(
                    usuarioId = usuarioActual.id,
                    tipoMaterial = currentState.materialSeleccionado,
                    pesoKg = peso,
                    ubicacion = currentState.ubicacionSeleccionada
                )
                _uiState.update {
                    it.copy(
                        isProcesando = false,
                        registroCompletado = registro,
                        pesoInput = ""
                    )
                }
                onSuccess(registro)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcesando = false,
                        errorMessage = e.message ?: "Error al procesar la entrega de reciclaje"
                    )
                }
            }
        }
    }

    fun limpiarRegistroCompletado() {
        _uiState.update { it.copy(registroCompletado = null) }
    }
}
