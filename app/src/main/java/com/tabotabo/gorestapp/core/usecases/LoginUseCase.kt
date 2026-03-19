package com.tabotabo.gorestapp.core.usecases

import com.tabotabo.gorestapp.core.data.local.auth.CryptoManager
import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.core.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val cryptoManager: CryptoManager,
) {
    suspend fun login(username: String, password: String): User? {
        // 1. Get user with credentials that will be verified
        val userWithCredentials = userRepository.getUserWithCredentials(username) ?: return null

        // 2. Generate login hash
        val loginHash = cryptoManager.hashPassword(password.toCharArray(), userWithCredentials.credentials.salt)

        // 3. Verify the hash
        val match = cryptoManager.slowEquals(loginHash, userWithCredentials.credentials.passwordHash)

        return if (match) {
            userRepository.insertNewUserSession(username)
            userWithCredentials.user.toDomain()
        } else {
            null
        }
    }
}