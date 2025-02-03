package com.ok.boys.delivery.base.api.orders

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class OrdersModule {

    @Provides
    @Singleton
    fun provideOrderRetrofitAPI(retrofit: Retrofit): OrdersRetrofitAPI {
        return retrofit.create(OrdersRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        ordersRetrofitAPI: OrdersRetrofitAPI,
    ): OrdersRepository {
        return OrdersRepository(ordersRetrofitAPI)
    }
}