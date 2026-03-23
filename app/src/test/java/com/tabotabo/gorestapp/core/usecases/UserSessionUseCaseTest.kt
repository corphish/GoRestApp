package com.tabotabo.gorestapp.core.usecases

import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.session.UserSessionEntity
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import com.tabotabo.gorestapp.core.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserSessionUseCaseTest {
    private lateinit var _userRepository: UserRepository
    private lateinit var _userSessionUseCase: UserSessionUseCase

    @Before
    fun setup() {
        _userRepository = mockk()
        _userSessionUseCase = UserSessionUseCase(_userRepository)
    }

    @Test
    fun `test no current user session`() = runTest {
        val user = UserEntity(
            username = "testUsername",
            name = "testName"
        )

        val userSession = UserSessionEntity(
            username = user.username,
            loginTime = System.currentTimeMillis()/2 // Simulate an old user session
        )

        coEvery { _userRepository.getCurrentSession() } returns userSession
        coEvery { _userRepository.getUserWithUsername(user.username) } returns user

        val currentUser = _userSessionUseCase.getCurrentUser()
        assertThat(currentUser).isNull()

        coVerify(exactly = 1) { _userRepository.getCurrentSession() }
        coVerify(exactly = 0) { _userRepository.getUserWithUsername(any()) }
    }

    @Test
    fun `test a day old current user session`() = runTest {
        val user = UserEntity(
            username = "testUsername",
            name = "testName"
        )

        val userSession = UserSessionEntity(
            username = user.username,
            loginTime = System.currentTimeMillis() - 24L * 60 * 60 * 1000 // Simulate a 1 day old user session
        )

        coEvery { _userRepository.getCurrentSession() } returns userSession
        coEvery { _userRepository.getUserWithUsername(user.username) } returns user

        val currentUser = _userSessionUseCase.getCurrentUser()
        assertThat(currentUser).isNotNull()
        assertThat(currentUser?.username).isEqualTo(user.username)

        coVerify(exactly = 1) { _userRepository.getCurrentSession() }
        coVerify(exactly = 1) { _userRepository.getUserWithUsername(any()) }
    }
}