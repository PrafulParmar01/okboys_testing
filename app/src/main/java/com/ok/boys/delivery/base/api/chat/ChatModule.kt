package com.ok.boys.delivery.base.api.chat

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ChatModule {

    @Provides
    @Singleton
    fun provideChatRetrofitAPI(retrofit: Retrofit): ChatRetrofitAPI {
        return retrofit.create(ChatRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatRetrofitAPI: ChatRetrofitAPI,
    ): ChatRepository {
        return ChatRepository(chatRetrofitAPI)
    }
}