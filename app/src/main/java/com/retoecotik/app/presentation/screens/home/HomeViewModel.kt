package com.retoecotik.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.ReciclajeRepository
import com.retoecotik.app.domain.repository.RetoRepository
import com.retoecotik.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val usuario: Usuario? = null,
    val totalKilosReciclados: Double = 0.0,
    val ultimasEntregas: List<RegistroReciclaje> = emptyList(),
    val retosActivos: List<Reto> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel para el dashboard de impacto y pantalla principal (SCR_03_HOME).
 */
class HomeViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val reciclajeRepository: ReciclajeRepository,
    private val retoRepository: RetoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarDatosDashboard()
    }

    private fun cargarDatosDashboard() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { usuario ->
                _uiState.update { it.copy(usuario = usuario) }
                if (usuario != null) {
                    observarHistorialYRetos(usuario.id)
                }
            }
        }
    }

    private fun observarHistorialYRetos(usuarioId: Long) {
        viewModelScope.launch {
            reciclajeRepository.obtenerHistorial(usuarioId).collect { lista ->
                val totalKilos = lista.sumOf { it.pesoKg }
                _uiState.update {
                    it.copy(
                        ultimasEntregas = lista.take(3),
                        totalKilosReciclados = totalKilos
                    )
                }
            }
        }

        viewModelScope.launch {
            retoRepository.obtenerRetosActivos().collect { retos ->
                _uiState.update { it.copy(retosActivos = retos.take(2)) }
            }
        }
    }
}
