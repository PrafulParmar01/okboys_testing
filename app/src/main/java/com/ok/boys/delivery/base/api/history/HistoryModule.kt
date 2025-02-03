package com.ok.boys.delivery.base.api.history

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class HistoryModule {

    @Provides
    @Singleton
    fun provideHistoryRetrofitAPI(retrofit: Retrofit): HistoryRetrofitAPI {
        return retrofit.create(HistoryRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        historyRetrofitAPI: HistoryRetrofitAPI,
    ): HistoryRepository {
        return HistoryRepository(historyRetrofitAPI)
    }
}