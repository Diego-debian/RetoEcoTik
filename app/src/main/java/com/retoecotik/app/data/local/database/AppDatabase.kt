package com.retoecotik.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.retoecotik.app.data.local.dao.RecompensaDao
import com.retoecotik.app.data.local.dao.RegistroReciclajeDao
import com.retoecotik.app.data.local.dao.RetoDao
import com.retoecotik.app.data.local.dao.UsuarioDao
import com.retoecotik.app.data.local.entity.RecompensaEntity
import com.retoecotik.app.data.local.entity.RegistroReciclajeEntity
import com.retoecotik.app.data.local.entity.RetoEntity
import com.retoecotik.app.data.local.entity.UsuarioEntity
import com.retoecotik.app.domain.security.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos local Room sobre SQLite para RetoEcoTik (DEC-025).
 * Provee la persistencia Local-First del MVP desacoplada del modelo corporativo MySQL de GA6.
 * Versión 2: Incorpora derivación segura PBKDF2WithHmacSHA256 con salt aleatorio en usuarios.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        RegistroReciclajeEntity::class,
        RetoEntity::class,
        RecompensaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun registroReciclajeDao(): RegistroReciclajeDao
    abstract fun retoDao(): RetoDao
    abstract fun recompensaDao(): RecompensaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migración explícita de versión 1 a 2: añade columna passwordSalt a la tabla usuarios.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN passwordSalt TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "retoecotik_mvp.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedInitialData(database)
                    }
                }
            }
        }

        /**
         * Inicializa datos demostrativos en el primer arranque para facilitar
         * la evaluación y sustentación sin requerir configuración externa.
         */
        suspend fun seedInitialData(database: AppDatabase) {
            val usuarioDao = database.usuarioDao()
            val retoDao = database.retoDao()
            val recompensaDao = database.recompensaDao()
            val reciclajeDao = database.registroReciclajeDao()

            if (usuarioDao.contarUsuarios() == 0) {
                // Usuario demo inicial (password "1234" con PBKDF2WithHmacSHA256 y salt aleatorio)
                val demoSaltBytes = PasswordHasher.generarSalt()
                val demoHashHex = PasswordHasher.hashear("1234", demoSaltBytes)
                val demoSaltHex = PasswordHasher.bytesToHex(demoSaltBytes)

                val demoUserId = usuarioDao.insertar(
                    UsuarioEntity(
                        nombre = "EcoExplorador Demo",
                        correo = "demo@retoecotik.com",
                        passwordHash = demoHashHex,
                        passwordSalt = demoSaltHex,
                        ecopuntosTotales = 150,
                        nivel = "Brote Activo",
                        co2EvitadoKg = 18.5,
                        esSesionActiva = true
                    )
                )

                // Entregas de historial previas demostrativas (UC-01)
                reciclajeDao.insertar(
                    RegistroReciclajeEntity(
                        usuarioId = demoUserId,
                        tipoMaterial = "PLASTICO",
                        pesoKg = 3.5,
                        ecopuntosGanados = 35,
                        co2EvitadoKg = 5.25,
                        fechaHoraMillis = System.currentTimeMillis() - 86400000L * 2,
                        descripcionUbicacion = "Punto Limpio Biblioteca"
                    )
                )

                reciclajeDao.insertar(
                    RegistroReciclajeEntity(
                        usuarioId = demoUserId,
                        tipoMaterial = "PAPEL_CARTON",
                        pesoKg = 5.0,
                        ecopuntosGanados = 30,
                        co2EvitadoKg = 4.5,
                        fechaHoraMillis = System.currentTimeMillis() - 86400000L,
                        descripcionUbicacion = "Contenedor Azul Bloque B"
                    )
                )
            }

            if (retoDao.contarRetos() == 0) {
                retoDao.insertarRetos(
                    listOf(
                        RetoEntity(
                            titulo = "Desafío PET Comunitario",
                            descripcion = "Recicla 10 kg de botellas plásticas durante el mes",
                            metaKg = 10.0,
                            progresoKg = 3.5,
                            recompensaPuntos = 100,
                            completado = false
                        ),
                        RetoEntity(
                            titulo = "Cero Cartón en Aula",
                            descripcion = "Acumula 15 kg de cartón para evitar tala de árboles",
                            metaKg = 15.0,
                            progresoKg = 5.0,
                            recompensaPuntos = 120,
                            completado = false
                        ),
                        RetoEntity(
                            titulo = "Campeón del Vidrio",
                            descripcion = "Recolecta 20 kg de botellas de vidrio retornables",
                            metaKg = 20.0,
                            progresoKg = 0.0,
                            recompensaPuntos = 150,
                            completado = false
                        )
                    )
                )
            }

            if (recompensaDao.contarRecompensas() == 0) {
                recompensaDao.insertarRecompensas(
                    listOf(
                        RecompensaEntity(
                            nombre = "Bolsa Ecológica de Tela",
                            descripcion = "Bolsa reutilizable 100% algodón orgánico con logo RetoEcoTik",
                            costoEcopuntos = 80,
                            stockDisponible = 25,
                            categoria = "Accesorios"
                        ),
                        RecompensaEntity(
                            nombre = "Termo de Acero Inoxidable",
                            descripcion = "Termo térmico de 500ml libre de BPA para reducir consumo de PET",
                            costoEcopuntos = 200,
                            stockDisponible = 10,
                            categoria = "Hogar"
                        ),
                        RecompensaEntity(
                            nombre = "Bono Cafetería Sostenible",
                            descripcion = "Descuento del 20% en refrigerios saludables locales",
                            costoEcopuntos = 120,
                            stockDisponible = 50,
                            categoria = "Bienestar"
                        )
                    )
                )
            }
        }
    }
}
