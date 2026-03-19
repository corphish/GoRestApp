package com.tabotabo.gorestapp.core.di

import android.content.Context
import androidx.room.Room
import com.tabotabo.gorestapp.core.data.local.AppDatabase
import com.tabotabo.gorestapp.core.data.local.auth.UserFunctionsDao
import com.tabotabo.gorestapp.core.data.local.session.UserSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_app_db"
        ).build()
    }

    @Provides
    fun provideUserFunctionsDao(db: AppDatabase): UserFunctionsDao {
        return db.userFunctionsDao()
    }

    @Provides
    fun provideUserSessionDao(db: AppDatabase): UserSessionDao {
        return db.userSessionDao()
    }
}