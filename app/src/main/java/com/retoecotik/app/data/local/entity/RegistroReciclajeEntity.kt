package com.retoecotik.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.retoecotik.app.domain.model.RegistroReciclaje
import com.retoecotik.app.domain.model.TipoMaterial

/**
 * Tabla local de pesajes y entregas de reciclaje (DEC-025 / UC-01).
 */
@Entity(tableName = "registros_reciclaje")
data class RegistroReciclajeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val tipoMaterial: String,
    val pesoKg: Double,
    val ecopuntosGanados: Int,
    val co2EvitadoKg: Double,
    val fechaHoraMillis: Long,
    val descripcionUbicacion: String
) {
    fun toDomain(): RegistroReciclaje = RegistroReciclaje(
        id = id,
        usuarioId = usuarioId,
        tipoMaterial = try {
            TipoMaterial.valueOf(tipoMaterial)
        } catch (_: Exception) {
            TipoMaterial.PLASTICO
        },
        pesoKg = pesoKg,
        ecopuntosGanados = ecopuntosGanados,
        co2EvitadoKg = co2EvitadoKg,
        fechaHoraMillis = fechaHoraMillis,
        descripcionUbicacion = descripcionUbicacion
    )

    companion object {
        fun fromDomain(registro: RegistroReciclaje): RegistroReciclajeEntity =
            RegistroReciclajeEntity(
                id = registro.id,
                usuarioId = registro.usuarioId,
                tipoMaterial = registro.tipoMaterial.name,
                pesoKg = registro.pesoKg,
                ecopuntosGanados = registro.ecopuntosGanados,
                co2EvitadoKg = registro.co2EvitadoKg,
                fechaHoraMillis = registro.fechaHoraMillis,
                descripcionUbicacion = registro.descripcionUbicacion
            )
    }
}
