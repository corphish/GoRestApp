package com.tabotabo.gorestapp.core.data.local.session

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.tabotabo.gorestapp.core.data.local.AppDatabase
import com.tabotabo.gorestapp.core.data.local.auth.UserFunctionsDao
import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserSessionDaoTest {
    private lateinit var _appDatabase: AppDatabase
    private lateinit var _userFunctionsDao: UserFunctionsDao
    private lateinit var _userSessionDao: UserSessionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        _appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        _userFunctionsDao = _appDatabase.userFunctionsDao()
        _userSessionDao = _appDatabase.userSessionDao()
    }

    @After
    fun cleanup() {
        _appDatabase.close()
    }

    @Test
    fun `no session test`() = runTest {
        assertThat(_userSessionDao.getCurrentUserSession()).isNull()
    }

    @Test
    fun `new session test`() = runTest {
        val user = UserEntity(
            username = "testUsername",
            name = "testName"
        )
        val session = UserSessionEntity(
            username = user.username,
            loginTime = System.currentTimeMillis()
        )

        _userFunctionsDao.insertUser(user)
        _userSessionDao.insertSession(session)

        assertThat(_userSessionDao.getCurrentUserSession()).isNotNull()
        assertThat(_userSessionDao.getCurrentUserSession()).isEqualTo(session)
        assertThat(_userSessionDao.getCurrentUserSession()?.username).isEqualTo(user.username)
    }

    @Test
    fun `session ordering`() = runTest {
        val user1 = UserEntity(
            username = "oldUsername",
            name = "User 1"
        )

        val user2 = UserEntity(
            username = "newUsername",
            name = "User 2"
        )

        val oldSession = UserSessionEntity(
            username = user1.username,
            loginTime = System.currentTimeMillis() - 100
        )

        val newSession = UserSessionEntity(
            username = user2.username,
            loginTime = System.currentTimeMillis()
        )

        _userFunctionsDao.insertUser(user1)
        _userFunctionsDao.insertUser(user2)

        _userSessionDao.insertSession(newSession)
        _userSessionDao.insertSession(oldSession)

        assertThat(_userSessionDao.getCurrentUserSession()).isNotNull()
        assertThat(_userSessionDao.getCurrentUserSession()).isEqualTo(newSession)
        assertThat(_userSessionDao.getCurrentUserSession()?.username).isEqualTo(user2.username)
    }
}