package com.tabotabo.gorestapp.core.network

import com.tabotabo.gorestapp.core.data.local.user.UserEntity
import retrofit2.http.GET

interface UserApiService {
    @GET("users")
    suspend fun getUsers(): List<UserEntity> // User is your Room Entity from before!
}