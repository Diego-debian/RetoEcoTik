package com.retoecotik.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.retoecotik.app.data.local.entity.RegistroReciclajeEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceso y persistencia de entregas de reciclaje (UC-01 / RF08).
 */
@Dao
interface RegistroReciclajeDao {

    @Query("SELECT * FROM registros_reciclaje WHERE usuarioId = :usuarioId ORDER BY fechaHoraMillis DESC")
    fun obtenerHistorialPorUsuario(usuarioId: Long): Flow<List<RegistroReciclajeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroReciclajeEntity): Long

    @Query("SELECT COALESCE(SUM(pesoKg), 0.0) FROM registros_reciclaje WHERE usuarioId = :usuarioId")
    suspend fun sumarKilosPorUsuario(usuarioId: Long): Double

    @Query("SELECT COALESCE(SUM(ecopuntosGanados), 0) FROM registros_reciclaje WHERE usuarioId = :usuarioId")
    suspend fun sumarPuntosPorUsuario(usuarioId: Long): Int

    @Query("SELECT COUNT(*) FROM registros_reciclaje WHERE usuarioId = :usuarioId")
    suspend fun contarRegistrosPorUsuario(usuarioId: Long): Int
}
