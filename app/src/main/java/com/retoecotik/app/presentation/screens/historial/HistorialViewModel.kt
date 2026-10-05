package com.retoecotik.app.presentation.screens.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.ReciclajeRepository
import com.retoecotik.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val usuario: Usuario? = null,
    val registros: List<RegistroReciclaje> = emptyList(),
    val totalKilos: Double = 0.0,
    val totalEcopuntosGanados: Int = 0,
    val isLoading: Boolean = true
)

/**
 * ViewModel para el historial detallado de entregas (SCR_06_HISTORIAL).
 */
class HistorialViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val reciclajeRepository: ReciclajeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { usuario ->
                _uiState.update { it.copy(usuario = usuario) }
                if (usuario != null) {
                    reciclajeRepository.obtenerHistorial(usuario.id).collect { lista ->
                        val kilos = lista.sumOf { it.pesoKg }
                        val puntos = lista.sumOf { it.ecopuntosGanados }
                        _uiState.update {
                            it.copy(
                                registros = lista,
                                totalKilos = kilos,
                                totalEcopuntosGanados = puntos,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
