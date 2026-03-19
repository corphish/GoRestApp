package com.tabotabo.gorestapp.core.data.local.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_sessions ORDER BY loginTime DESC LIMIT 1")
    suspend fun getCurrentUserSession(): UserSessionEntity?

    @Insert
    suspend fun insertSession(session: UserSessionEntity)
}