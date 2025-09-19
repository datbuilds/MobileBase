package vn.shb.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.shb.data.room.entity.UserInfoEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserInfoEntity)

    @Query("SELECT * FROM user_info")
    suspend fun getAllUsers(): List<UserInfoEntity>

    @Delete
    suspend fun delete(user: UserInfoEntity)
}