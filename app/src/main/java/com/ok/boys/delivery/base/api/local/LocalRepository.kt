package com.ok.boys.delivery.base.api.local


import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse
import com.ok.boys.delivery.db.LoginInfoDao
import io.reactivex.Completable
import io.reactivex.Single

class LocalRepository(private val loginInfoDao: LoginInfoDao) {

    fun saveLoginInfo(loginInfoEntity: GenerateLoginResponse): Completable {
        return loginInfoDao.insertLoginInfo(loginInfoEntity)
    }

    fun getLoginInfo(): Single<GenerateLoginResponse> {
        return loginInfoDao.getLoginInfo()
    }

    fun deleteTableData(): Completable {
        return loginInfoDao.deleteTableData()
    }
}