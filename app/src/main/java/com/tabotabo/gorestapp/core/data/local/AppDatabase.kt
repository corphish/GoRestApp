package com.tabotabo.gorestapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tabotabo.gorestapp.core.data.local.user.UserDao
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}