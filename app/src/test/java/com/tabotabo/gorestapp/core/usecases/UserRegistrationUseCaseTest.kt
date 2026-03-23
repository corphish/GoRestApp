package com.tabotabo.gorestapp.core.usecases

import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.auth.CryptoManager
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import com.tabotabo.gorestapp.core.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRegistrationUseCaseTest {
    private lateinit var _userRepository: UserRepository
    private lateinit var _userRegistrationUseCase: UserRegistrationUseCase
    private val _cryptoManager = CryptoManager()


    @Before
    fun setup() {
        _userRepository = mockk()
        _userRegistrationUseCase = UserRegistrationUseCase(
            _userRepository, _cryptoManager
        )
    }

    @Test
    fun `test user registration`() = runTest {
        val username = "testUsername"
        val displayName = "Test User"
        val password = "password"

        // We cannot verify password
        coEvery { _userRepository.registerUser(any(), any(), any(), any()) } returns UserEntity(
            username,
            displayName
        ).toDomain()

        assertThat(_userRegistrationUseCase.registerUser(username, displayName, password)).isNotNull()

        coVerify(exactly = 1) { _userRepository.registerUser(any(), any(), any(), any()) }
    }
}