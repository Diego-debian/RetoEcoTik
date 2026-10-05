package com.retoecotik.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.retoecotik.app.domain.model.Usuario

/**
 * Tabla local de usuarios en SQLite mediante Room (DEC-025).
 * Almacena el hash de la contraseña y el estado de la sesión activa en el dispositivo.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val passwordHash: String,
    val ecopuntosTotales: Int = 0,
    val nivel: String = "Semilla Verde",
    val co2EvitadoKg: Double = 0.0,
    val esSesionActiva: Boolean = false
) {
    fun toDomain(): Usuario = Usuario(
        id = id,
        nombre = nombre,
        correo = correo,
        passwordHash = passwordHash,
        ecopuntosTotales = ecopuntosTotales,
        nivel = nivel,
        co2EvitadoKg = co2EvitadoKg
    )

    companion object {
        fun fromDomain(usuario: Usuario, esSesionActiva: Boolean = false): UsuarioEntity =
            UsuarioEntity(
                id = usuario.id,
                nombre = usuario.nombre,
                correo = usuario.correo,
                passwordHash = usuario.passwordHash,
                ecopuntosTotales = usuario.ecopuntosTotales,
                nivel = usuario.nivel,
                co2EvitadoKg = usuario.co2EvitadoKg,
                esSesionActiva = esSesionActiva
            )
    }
}
