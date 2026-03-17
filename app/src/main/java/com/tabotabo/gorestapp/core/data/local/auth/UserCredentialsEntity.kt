package com.tabotabo.gorestapp.core.data.local.auth

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.tabotabo.gorestapp.core.data.local.user.UserEntity

@Entity(
    tableName = "user_credentials",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE // If user is deleted, credentials go too
        )
    ]
)
data class UserCredentialsEntity(
    @PrimaryKey
    val username: String,
    val passwordHash: ByteArray,
    val salt: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserCredentialsEntity

        if (username != other.username) return false
        if (!passwordHash.contentEquals(other.passwordHash)) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + passwordHash.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }
}