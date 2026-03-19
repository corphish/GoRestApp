package com.tabotabo.gorestapp.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tabotabo.gorestapp.core.data.local.user.UserExistsException
import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.core.usecases.LoginUseCase
import com.tabotabo.gorestapp.core.usecases.UserRegistrationUseCase
import com.tabotabo.gorestapp.core.usecases.UserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val userSessionUseCase: UserSessionUseCase,
): ViewModel() {
    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userUiState = _userUiState.asStateFlow()

    private val _userScreenModeState = MutableStateFlow(UserScreenMode.LOGIN)
    val userScreenModeState = _userScreenModeState.asStateFlow()

    // Text inputs
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var name = mutableStateOf("")

    init {
        viewModelScope.launch {
            initialize()
        }
    }

    private suspend fun initialize() {
        // Check for current user session
        val user = userSessionUseCase.getCurrentUser()

        if (user != null) {
            _userUiState.emit(UserUiState.Success(user))
        } else {
            _userUiState.emit(UserUiState.RequireUserInput)
        }
    }

    fun performLogin(username: String, inputPassword: String) {
        viewModelScope.launch {
            _userUiState.emit(UserUiState.Loading)

            val user = loginUseCase.login(username, inputPassword)
            if (user != null) {
                _userUiState.emit(UserUiState.Success(user))
            } else {
                changeScreenMode(UserScreenMode.LOGIN, clearUserInput = false)
                _userUiState.emit(UserUiState.Error("Invalid username/password"))
            }
        }
    }

    fun performRegistration(username: String, displayName: String, password: String) {
        viewModelScope.launch {
            _userUiState.emit(UserUiState.Loading)

            try {
                userRegistrationUseCase.registerUser(username, displayName, password)

                // We ask the user to login
                changeScreenMode(UserScreenMode.LOGIN)
                _userUiState.emit(UserUiState.RequireUserInput)
            } catch (_: UserExistsException) {
                changeScreenMode(UserScreenMode.REGISTER, clearUserInput = false)
                _userUiState.emit(UserUiState.Error("Username already exists"))
            }
        }
    }

    fun changeScreenMode(mode: UserScreenMode, clearUserInput: Boolean = true) {
        _userScreenModeState.value = mode

        // Clear inputs
        if (clearUserInput) {
            username.value = ""
            password.value = ""
            name.value = ""
        }
    }
}

sealed class UserUiState {
    data class Success(val user: User): UserUiState()
    object RequireUserInput: UserUiState()
    data class Error(val reason: String): UserUiState()
    object Loading: UserUiState()
}

enum class UserScreenMode {
    LOGIN,
    REGISTER
}