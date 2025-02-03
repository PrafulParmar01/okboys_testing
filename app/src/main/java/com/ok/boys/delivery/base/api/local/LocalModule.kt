package com.ok.boys.delivery.base.api.local

import com.ok.boys.delivery.db.LoginInfoDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Provides
    @Singleton
    fun provideLocalRepository(
        loginInfoDao: LoginInfoDao
    ): LocalRepository {
        return LocalRepository(loginInfoDao)
    }

}