package com.tabotabo.gorestapp.core.repository

import com.tabotabo.gorestapp.core.data.local.auth.CryptoManager
import com.tabotabo.gorestapp.core.data.local.auth.UserCredentialsEntity
import com.tabotabo.gorestapp.core.data.local.auth.UserFunctionsDao
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import com.tabotabo.gorestapp.core.domain.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userFunctionsDao: UserFunctionsDao,
    private val cryptoManager: CryptoManager
) {

    suspend fun isUserWithUsernamePresent(username: String) =
        userFunctionsDao.getUserUsingUsername(username) != null

    suspend fun registerUser(
        username: String,
        displayName: String,
        inputPassword: String,
    ): User? {
        // 1. Generate salt
        val salt = cryptoManager.generateSalt()

        // 2. Hash the password
        val hashedPassword = cryptoManager.hashPassword(inputPassword.toCharArray(), salt)

        // 3. Save to room
        val user = UserEntity(
            username = username,
            name = displayName
        )

        val credentials = UserCredentialsEntity(
            username = username,
            salt = salt,
            passwordHash = hashedPassword
        )

        userFunctionsDao.insertUser(user)
        userFunctionsDao.insertCredentials(credentials)

        return user.toDomain()
    }

    suspend fun login(username: String, password: String): User? {
        // 1. Get user with credentials that will be verified
        val userWithCredentials = userFunctionsDao.getUserWithCredentials(username) ?: return null

        // 2. Generate login hash
        val loginHash = cryptoManager.hashPassword(password.toCharArray(), userWithCredentials.credentials.salt)

        // 3. Verify the hash
        val match = cryptoManager.slowEquals(loginHash, userWithCredentials.credentials.passwordHash)

        return if (match) userWithCredentials.user.toDomain() else null
    }
}