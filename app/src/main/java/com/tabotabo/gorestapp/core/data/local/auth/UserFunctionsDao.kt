package com.tabotabo.gorestapp.core.data.local.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

/**
 * Supported functions
 */
@Dao
interface UserFunctionsDao {
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserWithCredentials(username: String): UserWithCredentials?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserUsingUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(credentials: UserCredentialsEntity)
}