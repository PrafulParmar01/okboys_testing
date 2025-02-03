package com.ok.boys.delivery.db

import androidx.room.*
import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse
import io.reactivex.Completable
import io.reactivex.Single

/*const val TABLE_LOGIN_INFO = "table_login_info"
const val USER_ID = "user_id"
const val LOGIN_RESPONSE = "login_response"*/

/*@Entity(
    tableName = TABLE_LOGIN_INFO,
    primaryKeys = [USER_ID],
    indices = [Index(USER_ID)]
)*/

/*@Keep
data class LoginInfoEntity(
    @ColumnInfo(name = USER_ID) var userId: String = "",
    @ColumnInfo(name = LOGIN_RESPONSE) var loginResponse: LoginDataResponse,
)*/

@Dao
interface LoginInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoginInfo(loginInfoEntity: GenerateLoginResponse): Completable

    @Query("SELECT * FROM GenerateLoginResponse")
    fun getLoginInfo(): Single<GenerateLoginResponse>

    @Query("DELETE FROM GenerateLoginResponse")
    fun deleteTableData(): Completable
}