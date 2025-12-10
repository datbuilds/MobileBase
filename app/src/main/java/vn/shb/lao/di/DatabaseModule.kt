package vn.shb.lao.di

import androidx.room.Room
import org.koin.dsl.module
import vn.shb.data.room.AppDatabase

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "my_database SHB_LAO"
        ).build()
    }

    single {
        get<AppDatabase>().userDao()
    }
}