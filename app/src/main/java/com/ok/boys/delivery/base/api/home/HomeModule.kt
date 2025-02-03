package com.ok.boys.delivery.base.api.home

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class HomeModule {

    @Provides
    @Singleton
    fun provideHomeRetrofitAPI(retrofit: Retrofit): HomeRetrofitAPI {
        return retrofit.create(HomeRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRetrofitAPI: HomeRetrofitAPI,
    ): HomeRepository {
        return HomeRepository(homeRetrofitAPI)
    }
}