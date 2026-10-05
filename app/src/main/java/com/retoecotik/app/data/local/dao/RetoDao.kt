package com.retoecotik.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.retoecotik.app.data.local.entity.RetoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para retos de gamificación comunitaria (RF11, RF12).
 */
@Dao
interface RetoDao {

    @Query("SELECT * FROM retos ORDER BY completado ASC, id ASC")
    fun obtenerTodosLosRetosFlow(): Flow<List<RetoEntity>>

    @Query("SELECT * FROM retos WHERE completado = 0")
    suspend fun obtenerRetosActivos(): List<RetoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRetos(retos: List<RetoEntity>)

    @Update
    suspend fun actualizarReto(reto: RetoEntity)

    @Query("SELECT COUNT(*) FROM retos")
    suspend fun contarRetos(): Int
}
