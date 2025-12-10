package vn.shb.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import vn.shb.data.room.entity.UserInfoEntity

@Dao
interface UserDao {

//    // Lấy tất cả user
//    @Query("SELECT * FROM user_info")
//    suspend fun getAllUsers(): List<UserInfoEntity>
//
//    // Lấy user theo id
//    @Query("SELECT * FROM user_info WHERE id = :id LIMIT 1")
//    suspend fun getUserById(id: Int): UserInfoEntity?
//
//    // Insert 1 user
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUser(user: UserInfoEntity)
//
//    // Insert nhiều user
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUsers(users: List<UserInfoEntity>)
//
//    // Update user
//    @Update
//    suspend fun updateUser(user: UserInfoEntity)
//
//    // Delete 1 user
//    @Delete
//    suspend fun deleteUser(user: UserInfoEntity)
//
//    // Xóa tất cả user
//    @Query("DELETE FROM user_info")
//    suspend fun deleteAll()
}
