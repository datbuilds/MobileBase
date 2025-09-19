package vn.shb.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "access_token")
    val accessToken: String,

    @ColumnInfo(name = "expires_in")
    val expiresIn: Int,

    @ColumnInfo(name = "refresh_expires_in")
    val refreshExpiresIn: Int,

    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,

    @ColumnInfo(name = "token_type")
    val tokenType: String,

    @ColumnInfo(name = "id_token")
    val idToken: String,

    @ColumnInfo(name = "session_state")
    val sessionState: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "user_log")
    val userLog: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "scope")
    val scope: String,

    @ColumnInfo(name = "image_base64")
    val imageBase64: String
)