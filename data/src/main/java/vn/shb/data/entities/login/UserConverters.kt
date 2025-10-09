package vn.shb.data.entities.login

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserConverters {

    private val gson = Gson()               // tái sử dụng để tránh tốn GC

    @TypeConverter
    fun userInfoToString(userLog: UserLog?): String {
        // Null safety – Room có thể truyền null
        return gson.toJson(userLog)
    }

    @TypeConverter
    fun stringToUserInfo(data: String?): UserLog? {
        if (data.isNullOrEmpty()) return null
        val type = object : TypeToken<UserLog>() {}.type
        return gson.fromJson(data, type)
    }
}
