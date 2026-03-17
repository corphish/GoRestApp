package com.tabotabo.gorestapp.core.data.local.auth

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    private val _secureRandom = SecureRandom()

    fun generateSalt(length: Int = 16): ByteArray {
        val salt = ByteArray(length)
        _secureRandom.nextBytes(salt)
        return salt
    }

    /**
     * Helper to compare two byte arrays securely to prevent timing attacks.
     */
    fun slowEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
        }
        return diff == 0
    }

    /**
     * Hashes a password using PBKDF2.
     * @param password The raw password (use CharArray to allow clearing from memory)
     * @param salt The unique salt generated for the user
     */
    fun hashPassword(password: CharArray, salt: ByteArray): ByteArray {
        val iterations = 65536 // Higher is slower/more secure
        val keyLength = 256    // Bit length of the resulting hash

        val spec: KeySpec = PBEKeySpec(password, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        return factory.generateSecret(spec).encoded
    }
}