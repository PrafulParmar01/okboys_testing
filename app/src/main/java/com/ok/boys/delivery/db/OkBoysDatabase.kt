package com.ok.boys.delivery.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse

@Database(
    entities = [(GenerateLoginResponse::class)],
    version = 2, exportSchema = false
)

@TypeConverters(CustomTypeConverter::class)
abstract class OkBoysDatabase : RoomDatabase() {
    abstract fun loginInfoDao(): LoginInfoDao
}
