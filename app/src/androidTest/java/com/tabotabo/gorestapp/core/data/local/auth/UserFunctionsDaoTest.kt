package com.tabotabo.gorestapp.core.data.local.auth

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.AppDatabase
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFunctionsDaoTest {
    private lateinit var _appDatabase: AppDatabase
    private lateinit var _userFunctionsDao: UserFunctionsDao
    private val _cryptoManager = CryptoManager()

    @Before
    fun setup() {
        val  context = ApplicationProvider.getApplicationContext<Context>()
        _appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        _userFunctionsDao = _appDatabase.userFunctionsDao()
    }

    @After
    fun cleanup() {
        _appDatabase.close()
    }

    @Test
    fun `user test`() = runTest {
        val user = UserEntity(
            username = "testUserName",
            name = "testName"
        )

        _userFunctionsDao.insertUser(user)

        assertThat(_userFunctionsDao.getUserUsingUsername("testUserName")).isEqualTo(user)
        assertThat(_userFunctionsDao.getUserUsingUsername("otherUserName")).isNull()
    }

    @Test
    fun `user credentials test`() = runTest {
        val user = UserEntity(
            username = "testUserName",
            name = "testName"
        )

        _userFunctionsDao.insertUser(user)

        val userCredentials = UserCredentialsEntity(
            username = user.username,
            salt = _cryptoManager.generateSalt(),
            passwordHash = _cryptoManager.generateSalt()
        )

        _userFunctionsDao.insertCredentials(userCredentials)

        val userWithCredentials = UserWithCredentials(
            user = user,
            credentials = userCredentials
        )

        assertThat(_userFunctionsDao.getUserWithCredentials(user.username)).isEqualTo(userWithCredentials)
        assertThat(_userFunctionsDao.getUserWithCredentials("otherUsername")).isNull()
    }
}