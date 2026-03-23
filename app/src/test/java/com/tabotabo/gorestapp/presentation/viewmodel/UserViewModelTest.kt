package com.tabotabo.gorestapp.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.user.UserExistsException
import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.core.usecases.LoginUseCase
import com.tabotabo.gorestapp.core.usecases.UserRegistrationUseCase
import com.tabotabo.gorestapp.core.usecases.UserSessionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val _loginUseCase = mockk<LoginUseCase>()
    private val _userSessionUseCase = mockk<UserSessionUseCase>()
    private val _userRegistrationUseCase = mockk<UserRegistrationUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init test with no current user`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        // Elapse time
        advanceUntilIdle()

        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.RequireUserInput::class.java)

        coVerify(exactly = 1) { _userSessionUseCase.getCurrentUser() }
    }

    @Test
    fun `init test with a current user`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns User(
            username = "testUsername",
            displayName = "testName"
        )

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        // Elapse time
        advanceUntilIdle()

        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.Success::class.java)
        assertThat(viewModel.userUiState.value).isEqualTo(UserUiState.Success(User(
            username = "testUsername",
            displayName = "testName"
        )))

        coVerify(exactly = 1) { _userSessionUseCase.getCurrentUser() }
    }

    @Test
    fun `login success test`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null
        coEvery { _loginUseCase.login(any(), any()) } returns User(
            username = "testUsername",
            displayName = "testName"
        )

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        // Elapse time
        advanceUntilIdle()

        viewModel.performLogin("testUsername", "testPassword")

        advanceUntilIdle()

        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.Success::class.java)
        assertThat(viewModel.userUiState.value).isEqualTo(UserUiState.Success(User(
            username = "testUsername",
            displayName = "testName"
        )))

        coVerify(exactly = 1) { _loginUseCase.login(any(), any()) }
    }

    @Test
    fun `login failure test`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null
        coEvery { _loginUseCase.login(any(), any()) } returns null

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        // Elapse time
        advanceUntilIdle()

        viewModel.performLogin("testUsername", "testPassword")

        advanceUntilIdle()

        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.Error::class.java)
        assertThat(viewModel.userUiState.value).isEqualTo(UserUiState.Error("Invalid username/password"))
        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.LOGIN)

        coVerify(exactly = 1) { _loginUseCase.login(any(), any()) }
    }

    @Test
    fun `registration success test`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null
        coEvery { _userRegistrationUseCase.registerUser(any(), any(), any()) } returns User(
            username = "testUsername",
            displayName = "testName"
        )

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        advanceUntilIdle()

        viewModel.performRegistration("test", "test", "test")

        advanceUntilIdle()

        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.LOGIN)
        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.RequireUserInput::class.java)

        coVerify(exactly = 1) { _userRegistrationUseCase.registerUser(any(), any(), any()) }
    }

    @Test
    fun `registration fail test`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null
        coEvery { _userRegistrationUseCase.registerUser(any(), any(), any()) } throws UserExistsException()

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        advanceUntilIdle()

        viewModel.performRegistration("test", "test", "test")

        advanceUntilIdle()

        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.REGISTER)
        assertThat(viewModel.userUiState.value).isInstanceOf(UserUiState.Error::class.java)

        coVerify(exactly = 1) { _userRegistrationUseCase.registerUser(any(), any(), any()) }
    }

    @Test
    fun `test screen mode change`() = runTest {
        coEvery { _userSessionUseCase.getCurrentUser() } returns null

        val viewModel = UserViewModel(
            _loginUseCase,
            _userRegistrationUseCase,
            _userSessionUseCase
        )

        advanceUntilIdle()

        viewModel.changeScreenMode(UserScreenMode.REGISTER)
        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.REGISTER)

        viewModel.changeScreenMode(UserScreenMode.LOGIN)
        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.LOGIN)

        viewModel.name.value = "test"
        viewModel.username.value = "test"
        viewModel.password.value = "test"

        assertThat(viewModel.name.value).isEqualTo("test")
        assertThat(viewModel.username.value).isEqualTo("test")
        assertThat(viewModel.password.value).isEqualTo("test")

        viewModel.changeScreenMode(UserScreenMode.LOGIN, clearUserInput = false)
        assertThat(viewModel.userScreenModeState.value).isEqualTo(UserScreenMode.LOGIN)
        assertThat(viewModel.name.value).isEqualTo("test")
        assertThat(viewModel.username.value).isEqualTo("test")
        assertThat(viewModel.password.value).isEqualTo("test")
    }
}