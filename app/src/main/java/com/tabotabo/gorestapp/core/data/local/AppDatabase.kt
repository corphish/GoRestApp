package com.tabotabo.gorestapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tabotabo.gorestapp.core.data.local.auth.UserCredentialsEntity
import com.tabotabo.gorestapp.core.data.local.auth.UserFunctionsDao
import com.tabotabo.gorestapp.core.data.local.session.UserSessionDao
import com.tabotabo.gorestapp.core.data.local.session.UserSessionEntity
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

@Database(
    entities = [UserEntity::class, UserCredentialsEntity::class, UserSessionEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userFunctionsDao(): UserFunctionsDao
    abstract fun userSessionDao(): UserSessionDao
}