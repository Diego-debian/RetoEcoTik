package com.retoecotik.app.domain.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Servicio de derivación segura de contraseñas para el MVP local de RetoEcoTik (STD-SEC-01).
 * Implementa PBKDF2WithHmacSHA256 con salt aleatorio generado por SecureRandom.
 *
 * Parámetros técnicos calibrados por benchmark en emulador Android API 35:
 * - ALGORITHM: PBKDF2WithHmacSHA256
 * - ITERATIONS: 100_000 iteraciones (benchmark: ~245ms hash, ~289ms verify en ART x86_64)
 * - SALT_SIZE: 16 bytes (128 bits)
 * - KEY_LENGTH: 256 bits (32 bytes)
 */
object PasswordHasher {

    const val ALGORITHM = "PBKDF2WithHmacSHA256"
    const val ITERATIONS = 100_000
    const val SALT_SIZE_BYTES = 16
    const val KEY_LENGTH_BITS = 256

    /**
     * Genera un salt criptográficamente seguro de 16 bytes.
     */
    fun generarSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_SIZE_BYTES)
        random.nextBytes(salt)
        return salt
    }

    /**
     * Deriva una clave a partir de la contraseña plana y el salt mediante PBKDF2WithHmacSHA256.
     * Retorna el hash derivado en representación hexadecimal.
     */
    fun hashear(passwordPlano: String, salt: ByteArray): String {
        val spec = PBEKeySpec(passwordPlano.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded
        return bytesToHex(hash)
    }

    /**
     * Compara una contraseña plana contra el hash esperado utilizando el salt almacenado.
     */
    fun verificar(passwordPlano: String, saltHex: String, expectedHashHex: String): Boolean {
        if (saltHex.isBlank() || expectedHashHex.isBlank()) return false
        val salt = hexToBytes(saltHex)
        val calculatedHash = hashear(passwordPlano, salt)
        return calculatedHash.equals(expectedHashHex, ignoreCase = true)
    }

    fun bytesToHex(bytes: ByteArray): String =
        bytes.joinToString("") { "%02x".format(it) }

    fun hexToBytes(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "Cadena hexadecimal inválida" }
        val result = ByteArray(hex.length / 2)
        for (i in result.indices) {
            val index = i * 2
            result[i] = hex.substring(index, index + 2).toInt(16).toByte()
        }
        return result
    }
}
