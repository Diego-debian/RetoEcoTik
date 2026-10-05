package com.retoecotik.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.retoecotik.app.domain.model.Reto

/**
 * Tabla local de retos ambientales (DEC-025 / RF11).
 */
@Entity(tableName = "retos")
data class RetoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val metaKg: Double,
    val progresoKg: Double,
    val recompensaPuntos: Int,
    val completado: Boolean
) {
    fun toDomain(): Reto = Reto(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        metaKg = metaKg,
        progresoKg = progresoKg,
        recompensaPuntos = recompensaPuntos,
        completado = completado
    )

    companion object {
        fun fromDomain(reto: Reto): RetoEntity = RetoEntity(
            id = reto.id,
            titulo = reto.titulo,
            descripcion = reto.descripcion,
            metaKg = reto.metaKg,
            progresoKg = reto.progresoKg,
            recompensaPuntos = reto.recompensaPuntos,
            completado = reto.completado
        )
    }
}
