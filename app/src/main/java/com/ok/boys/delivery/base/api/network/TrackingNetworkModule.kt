package com.ok.boys.delivery.base.api.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.ok.boys.delivery.BuildConfig
import com.ok.boys.delivery.util.ApiConstant
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


open class TrackingNetworkModule {

    fun getInstance(mContext: Context): Retrofit {
        val cacheSize = 10 * 1024 * 1024
        val cacheDir = File(mContext.cacheDir, "HttpCache")
        val cache = Cache(cacheDir, cacheSize.toLong())
        val builder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(RetrofitInterceptorHeaders(mContext))

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(builder.build())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .enableComplexMapKeySerialization()
                        .create()
                )
            )
            .build()
    }
}