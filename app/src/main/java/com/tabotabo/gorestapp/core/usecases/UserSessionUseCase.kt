package com.tabotabo.gorestapp.core.usecases

import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.core.repository.UserRepository
import javax.inject.Inject

class UserSessionUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private val _ttl = 7 * 24 * 60 * 60 * 1000L
    suspend fun getCurrentUser(): User? {
        val session = userRepository.getCurrentSession() ?: return null
        return if (System.currentTimeMillis() - session.loginTime >= _ttl) {
            // Expired
            null
        } else {
            userRepository.getUserWithUsername(session.username)?.toDomain()
        }
    }
}