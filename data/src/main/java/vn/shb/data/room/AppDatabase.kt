package vn.shb.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.shb.data.room.dao.UserDao
import vn.shb.data.room.entity.UserInfoEntity

@Database(entities = [UserInfoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}