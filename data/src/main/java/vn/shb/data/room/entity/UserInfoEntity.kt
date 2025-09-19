package vn.shb.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val access_token: String = "",
    val expires_in: Int = 0,
    val refresh_expires_in: Int = 0,
    val refresh_token: String = "",
    val token_type: String = "",
    val id_token: String = "",
    val session_state: String = "",
    val username: String = "",
    val userLog: String = "",
    val title: String = "",
    val scope: String = "",
    val imageBase64: String = ""
)