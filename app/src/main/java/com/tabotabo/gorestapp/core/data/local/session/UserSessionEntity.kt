package com.tabotabo.gorestapp.core.data.local.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

@Entity(
    tableName = "user_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE // If user is deleted, credentials go too
        )
    ]
)
data class UserSessionEntity(
    @PrimaryKey
    val username: String,

    // This indicates last login time
    val loginTime: Long = System.currentTimeMillis()
)
