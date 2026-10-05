package com.retoecotik.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.retoecotik.app.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceso y mutación de usuarios en SQLite (Room).
 */
@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE esSesionActiva = 1 LIMIT 1")
    fun obtenerUsuarioActivoFlow(): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuarios WHERE esSesionActiva = 1 LIMIT 1")
    suspend fun obtenerUsuarioActivo(): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Query("UPDATE usuarios SET esSesionActiva = 0")
    suspend fun cerrarTodasLasSesiones()

    @Query("UPDATE usuarios SET esSesionActiva = 1 WHERE id = :usuarioId")
    suspend fun activarSesion(usuarioId: Long)

    @Query("UPDATE usuarios SET ecopuntosTotales = ecopuntosTotales + :puntos, co2EvitadoKg = co2EvitadoKg + :co2Kg WHERE id = :usuarioId")
    suspend fun incrementarBalance(usuarioId: Long, puntos: Int, co2Kg: Double)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
