package com.tabotabo.gorestapp.core.data.local.auth

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CryptoManagerTest {
    private val _cryptoManager = CryptoManager()

    @Test
    fun `size of generated salt`() {
        val salt = _cryptoManager.generateSalt()
        val saltOfLength32 = _cryptoManager.generateSalt(length = 32)

        assertThat(salt.size).isEqualTo(16)
        assertThat(saltOfLength32.size).isEqualTo(32)
    }

    @Test
    fun `salt content verification`() {
        val salt = _cryptoManager.generateSalt()
        val anotherSalt = _cryptoManager.generateSalt()
        val anotherSaltOfDifferentLength = _cryptoManager.generateSalt(length = 8)

        assertThat(_cryptoManager.slowEquals(salt, salt)).isTrue()
        assertThat(_cryptoManager.slowEquals(salt, anotherSalt)).isFalse()
        assertThat(_cryptoManager.slowEquals(anotherSalt, anotherSaltOfDifferentLength)).isFalse()
    }

    @Test
    fun `password verification`() {
        val password = "password".toCharArray()

        val salt = _cryptoManager.generateSalt()
        val anotherSalt = _cryptoManager.generateSalt()

        assertThat(_cryptoManager.hashPassword(password, salt))
            .isNotEqualTo(_cryptoManager.hashPassword(password, anotherSalt))
    }
}