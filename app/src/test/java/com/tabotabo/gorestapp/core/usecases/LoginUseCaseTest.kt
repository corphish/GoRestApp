package com.tabotabo.gorestapp.core.usecases

import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.auth.CryptoManager
import com.tabotabo.gorestapp.core.data.local.auth.UserCredentialsEntity
import com.tabotabo.gorestapp.core.data.local.auth.UserWithCredentials
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import com.tabotabo.gorestapp.core.repository.UserRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {
    private lateinit var _userRepository: UserRepository
    private val _cryptoManager = CryptoManager()
    private lateinit var _loginUseCase: LoginUseCase

    @Before
    fun setup() {
        _userRepository = mockk()
        _loginUseCase = LoginUseCase(
            _userRepository, _cryptoManager
        )
    }

    @Test
    fun `login test`() = runTest {
        // First setup a user and credentials entity
        val registeredUser = UserEntity(
            username = "registeredUser",
            name = "Registered User"
        )

        val password = "password"

        val salt = _cryptoManager.generateSalt()
        val hash = _cryptoManager.hashPassword(password.toCharArray(), salt)
        val registeredUserCredentials = UserCredentialsEntity(
            username = registeredUser.username,
            salt = salt,
            passwordHash = hash
        )

        val userWithCredentials = UserWithCredentials(
            user = registeredUser,
            credentials = registeredUserCredentials
        )

        val otherUserWithCredentials = UserWithCredentials(
            user = UserEntity(
                name = "Other User",
                username = "otherUser"
            ),
            credentials = UserCredentialsEntity(
                username = "otherUser",
                salt = _cryptoManager.generateSalt(),
                passwordHash = _cryptoManager.generateSalt()
            )
        )

        coEvery { _userRepository.getUserWithCredentials(eq(registeredUser.username)) } returns userWithCredentials
        coEvery { _userRepository.getUserWithCredentials(not(registeredUser.username)) } returns otherUserWithCredentials

        coEvery { _userRepository.insertNewUserSession(any()) } just Runs

        val loggedInUser = _loginUseCase.login(registeredUser.username, password)
        val failCase = _loginUseCase.login(otherUserWithCredentials.user.username, password)

        assertThat(loggedInUser).isNotNull()
        assertThat(failCase).isNull()

        assertThat(loggedInUser?.username).isEqualTo(registeredUser.username)

        coVerify(exactly = 2) { _userRepository.getUserWithCredentials(any()) }
        coVerify(exactly = 1) { _userRepository.insertNewUserSession(any()) }
    }
}