package com.tabotabo.gorestapp.core.data.local.session

import androidx.room.Embedded
import androidx.room.Relation
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

data class UserWithSession(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "username",
        entityColumn = "username"
    )
    val session: UserSessionEntity
)