package com.retoecotik.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retoecotik.app.domain.model.Usuario
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.usecase.AutenticarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val nombre: String = "",
    val correo: String = "demo@retoecotik.com",
    val passwordPlano: String = "1234",
    val isLoading: Boolean = false,
    val usuarioAutenticado: Usuario? = null,
    val errorMessage: String? = null,
    val registroExitoso: Boolean = false
)

/**
 * ViewModel para autenticación y registro local (SCR_01_LOGIN / SCR_02_REGISTRO).
 * Cumple con STD-STF-01 (StateFlow inmutable público) y STD-COR-02 (viewModelScope).
 */
class AuthViewModel(
    private val autenticarUsuarioUseCase: AutenticarUsuarioUseCase,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        verificarSesionExistente()
    }

    private fun verificarSesionExistente() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { usuario ->
                _uiState.update { it.copy(usuarioAutenticado = usuario) }
            }
        }
    }

    fun onNombreChanged(nuevo: String) {
        _uiState.update { it.copy(nombre = nuevo, errorMessage = null) }
    }

    fun onCorreoChanged(nuevo: String) {
        _uiState.update { it.copy(correo = nuevo, errorMessage = null) }
    }

    fun onPasswordChanged(nuevo: String) {
        _uiState.update { it.copy(passwordPlano = nuevo, errorMessage = null) }
    }

    fun iniciarSesion(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.correo.isBlank() || currentState.passwordPlano.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingrese correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val usuario = autenticarUsuarioUseCase.iniciarSesion(
                    currentState.correo,
                    currentState.passwordPlano
                )
                if (usuario != null) {
                    _uiState.update { it.copy(isLoading = false, usuarioAutenticado = usuario) }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Credenciales incorrectas. Pruebe demo@retoecotik.com / 1234"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al autenticar usuario local"
                    )
                }
            }
        }
    }

    fun registrarUsuario(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                autenticarUsuarioUseCase.registrarUsuario(
                    currentState.nombre,
                    currentState.correo,
                    currentState.passwordPlano
                )
                _uiState.update { it.copy(isLoading = false, registroExitoso = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al registrar nuevo usuario"
                    )
                }
            }
        }
    }

    fun cerrarSesion(onLogout: () -> Unit) {
        viewModelScope.launch {
            usuarioRepository.cerrarSesion()
            _uiState.update { it.copy(usuarioAutenticado = null) }
            onLogout()
        }
    }
}
