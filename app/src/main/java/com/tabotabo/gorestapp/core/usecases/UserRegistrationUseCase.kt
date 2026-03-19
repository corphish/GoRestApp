package com.tabotabo.gorestapp.core.usecases

import com.tabotabo.gorestapp.core.data.local.auth.CryptoManager
import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.core.repository.UserRepository
import javax.inject.Inject

class UserRegistrationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val cryptoManager: CryptoManager,
) {
    suspend fun registerUser(
        username: String,
        displayName: String,
        inputPassword: String
    ): User {
        val salt = cryptoManager.generateSalt()
        val passwordHash = cryptoManager.hashPassword(inputPassword.toCharArray(), salt)
        return userRepository.registerUser(
            username = username,
            displayName = displayName,
            salt = salt,
            passwordHash = passwordHash
        )
    }
}