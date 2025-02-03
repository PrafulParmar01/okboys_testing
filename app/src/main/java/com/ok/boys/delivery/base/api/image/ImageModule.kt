package com.ok.boys.delivery.base.api.image

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ImageModule {

    @Provides
    @Singleton
    fun provideImageRetrofitAPI(retrofit: Retrofit): ImageRetrofitAPI {
        return retrofit.create(ImageRetrofitAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        imageRetrofitAPI: ImageRetrofitAPI,
    ): ImageRepository {
        return ImageRepository(imageRetrofitAPI)
    }
}