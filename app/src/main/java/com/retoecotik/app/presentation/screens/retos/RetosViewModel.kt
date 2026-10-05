package com.retoecotik.app.presentation.screens.retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retoecotik.app.domain.model.Recompensa
import com.retoecotik.app.domain.model.Reto
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.RetoRepository
import com.retoecotik.app.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RetosUiState(
    val usuario: Usuario? = null,
    val retos: List<Reto> = emptyList(),
    val recompensas: List<Recompensa> = emptyList(),
    val mensajeCanje: String? = null
)

/**
 * ViewModel para el módulo de retos comunitarios y catálogo de recompensas (SCR_05_RETOS / SCR_07_RECOMPENSAS).
 */
class RetosViewModel(
    private val retoRepository: RetoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { user ->
                _uiState.update { it.copy(usuario = user) }
            }
        }

        viewModelScope.launch {
            retoRepository.obtenerRetosActivos().collect { retos ->
                _uiState.update { it.copy(retos = retos) }
            }
        }

        viewModelScope.launch {
            retoRepository.obtenerRecompensasDisponibles().collect { recompensas ->
                _uiState.update { it.copy(recompensas = recompensas) }
            }
        }
    }

    fun canjearRecompensa(recompensa: Recompensa) {
        val user = _uiState.value.usuario
        if (user == null) {
            _uiState.update { it.copy(mensajeCanje = "Inicie sesión para canjear recompensas") }
            return
        }
        if (user.ecopuntosTotales < recompensa.costoEcopuntos) {
            val faltantes = recompensa.costoEcopuntos - user.ecopuntosTotales
            _uiState.update { it.copy(mensajeCanje = "Ecopuntos insuficientes. Te faltan $faltantes pts") }
            return
        }

        viewModelScope.launch {
            usuarioRepository.actualizarBalance(user.id, -recompensa.costoEcopuntos, 0.0)
            _uiState.update {
                it.copy(mensajeCanje = "¡Felicidades! Has canjeado '${recompensa.nombre}'. Reclama en tu punto ecológico.")
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeCanje = null) }
    }
}
