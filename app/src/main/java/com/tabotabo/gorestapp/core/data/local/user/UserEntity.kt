package com.tabotabo.gorestapp.core.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tabotabo.gorestapp.core.domain.User

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val username: String,
    val name: String
) {
    fun toDomain() = User(
        username = username,
        displayName = name
    )
}