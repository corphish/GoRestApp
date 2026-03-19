package com.tabotabo.gorestapp.core.repository

import com.tabotabo.gorestapp.core.data.local.auth.UserCredentialsEntity
import com.tabotabo.gorestapp.core.data.local.auth.UserFunctionsDao
import com.tabotabo.gorestapp.core.data.local.session.UserSessionDao
import com.tabotabo.gorestapp.core.data.local.session.UserSessionEntity
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import com.tabotabo.gorestapp.core.data.local.user.UserExistsException
import com.tabotabo.gorestapp.core.domain.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userFunctionsDao: UserFunctionsDao,
    private val userSessionDao: UserSessionDao
) {
    suspend fun getUserWithUsername(username: String) =
        userFunctionsDao.getUserUsingUsername(username)

    suspend fun registerUser(
        username: String,
        displayName: String,
        salt: ByteArray,
        passwordHash: ByteArray,
    ): User {
        val user = UserEntity(
            username = username,
            name = displayName
        )

        val credentials = UserCredentialsEntity(
            username = username,
            salt = salt,
            passwordHash = passwordHash
        )

        if (userFunctionsDao.getUserUsingUsername(username) != null) {
            throw UserExistsException()
        }

        userFunctionsDao.insertUser(user)
        userFunctionsDao.insertCredentials(credentials)

        return user.toDomain()
    }

    suspend fun getUserWithCredentials(username: String) =
        userFunctionsDao.getUserWithCredentials(username)

    suspend fun insertNewUserSession(username: String) {
        userSessionDao.insertSession(UserSessionEntity(username))
    }

    suspend fun getCurrentSession() =
        userSessionDao.getCurrentUserSession()
}