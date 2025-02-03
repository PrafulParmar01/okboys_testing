package com.ok.boys.delivery.base.api.login

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class LoginModule {

    @Provides
    @Singleton
    fun provideLoginRetrofitAPI(retrofit: Retrofit): LoginRetrofitAPI {
        return retrofit.create(LoginRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRecentlyPlayedRepository(
        loginRetrofitAPI: LoginRetrofitAPI,
    ): LoginRepository {
        return LoginRepository(loginRetrofitAPI)
    }
}