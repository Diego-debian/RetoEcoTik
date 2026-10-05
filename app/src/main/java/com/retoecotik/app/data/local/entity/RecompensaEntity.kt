package com.retoecotik.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.retoecotik.app.domain.model.Recompensa

/**
 * Tabla local de recompensas ambientales (DEC-025 / RF13).
 */
@Entity(tableName = "recompensas")
data class RecompensaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val costoEcopuntos: Int,
    val stockDisponible: Int,
    val categoria: String
) {
    fun toDomain(): Recompensa = Recompensa(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        costoEcopuntos = costoEcopuntos,
        stockDisponible = stockDisponible,
        categoria = categoria
    )

    companion object {
        fun fromDomain(recompensa: Recompensa): RecompensaEntity = RecompensaEntity(
            id = recompensa.id,
            nombre = recompensa.nombre,
            descripcion = recompensa.descripcion,
            costoEcopuntos = recompensa.costoEcopuntos,
            stockDisponible = recompensa.stockDisponible,
            categoria = recompensa.categoria
        )
    }
}
