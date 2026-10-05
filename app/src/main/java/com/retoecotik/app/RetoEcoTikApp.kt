package com.retoecotik.app

import android.app.Application
import com.retoecotik.app.data.local.database.AppDatabase
import com.retoecotik.app.data.repository.ReciclajeRepositoryImpl
import com.retoecotik.app.data.repository.RetoRepositoryImpl
import com.retoecotik.app.data.repository.UsuarioRepositoryImpl
import com.retoecotik.app.domain.repository.ReciclajeRepository
import com.retoecotik.app.domain.repository.RetoRepository
import com.retoecotik.app.domain.repository.UsuarioRepository
import com.retoecotik.app.domain.usecase.AutenticarUsuarioUseCase
import com.retoecotik.app.domain.usecase.CalcularEcopuntosUseCase
import com.retoecotik.app.domain.usecase.RegistrarReciclajeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Contenedor de dependencias a nivel de aplicación para RetoEcoTik.
 * Inicializa la base de datos Room y los repositorios singleton.
 */
class RetoEcoTikApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(database.usuarioDao())
    }

    val reciclajeRepository: ReciclajeRepository by lazy {
        ReciclajeRepositoryImpl(database.registroReciclajeDao())
    }

    val retoRepository: RetoRepository by lazy {
        RetoRepositoryImpl(database.retoDao(), database.recompensaDao())
    }

    val calcularEcopuntosUseCase by lazy { CalcularEcopuntosUseCase() }

    val registrarReciclajeUseCase by lazy {
        RegistrarReciclajeUseCase(
            reciclajeRepository = reciclajeRepository,
            usuarioRepository = usuarioRepository,
            retoRepository = retoRepository,
            calcularEcopuntosUseCase = calcularEcopuntosUseCase
        )
    }

    val autenticarUsuarioUseCase by lazy {
        AutenticarUsuarioUseCase(usuarioRepository)
    }
}
