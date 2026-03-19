package com.tabotabo.gorestapp.core.data.local.auth

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserCredentialsEntityTest {
    private val _cryptoManager = CryptoManager()

    @Test
    fun `validate equals method`() {
        val salt = _cryptoManager.generateSalt()
        val password = "password".toCharArray()
        val passwordHash = _cryptoManager.hashPassword(password, salt)

        val baseCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = salt
        )

        val sameCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = salt
        )

        val differentCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = _cryptoManager.generateSalt()
        )

        assertThat(baseCredentials).isEqualTo(baseCredentials)
        assertThat(baseCredentials).isEqualTo(sameCredentials)
        assertThat(baseCredentials).isNotEqualTo(differentCredentials)
    }

    @Test
    fun `validate hashcode method`() {
        val salt = _cryptoManager.generateSalt()
        val password = "password".toCharArray()
        val passwordHash = _cryptoManager.hashPassword(password, salt)

        val baseCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = salt
        )

        val sameCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = salt
        )

        val differentCredentials = UserCredentialsEntity(
            username = "username",
            passwordHash = passwordHash,
            salt = _cryptoManager.generateSalt()
        )

        assertThat(baseCredentials.hashCode()).isEqualTo(sameCredentials.hashCode())
        assertThat(baseCredentials.hashCode()).isNotEqualTo(differentCredentials.hashCode())
    }
}