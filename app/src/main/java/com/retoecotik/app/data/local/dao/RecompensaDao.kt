package com.retoecotik.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.retoecotik.app.data.local.entity.RecompensaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para recompensas e incentivos ecológicos (RF13, RF14).
 */
@Dao
interface RecompensaDao {

    @Query("SELECT * FROM recompensas WHERE stockDisponible > 0 ORDER BY costoEcopuntos ASC")
    fun obtenerRecompensasDisponiblesFlow(): Flow<List<RecompensaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRecompensas(recompensas: List<RecompensaEntity>)

    @Query("SELECT COUNT(*) FROM recompensas")
    suspend fun contarRecompensas(): Int
}
