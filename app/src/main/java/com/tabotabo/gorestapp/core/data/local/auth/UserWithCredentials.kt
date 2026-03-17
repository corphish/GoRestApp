package com.tabotabo.gorestapp.core.data.local.auth

import androidx.room.Embedded
import androidx.room.Relation
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

data class UserWithCredentials(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "username",
        entityColumn = "username"
    )
    val credentials: UserCredentialsEntity
)