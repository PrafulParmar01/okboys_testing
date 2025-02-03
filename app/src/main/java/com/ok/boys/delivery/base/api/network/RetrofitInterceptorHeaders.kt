package com.ok.boys.delivery.base.api.network

import android.content.Context
import android.util.Log
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.PrefUtil
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class RetrofitInterceptorHeaders(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        requestBuilder.header("Accept", "application/json")

        val accessToken = PrefUtil.getStringPref(PrefUtil.PRF_ACCESS_TOKEN, context)
        Log.e("accessToken: ", "===> $accessToken")

        if(ApiConstant.IS_TOKEN_PASSED) {
            requestBuilder.header("Authorization", "bearer $accessToken")
        }

        val response: Response
        try {
            response = chain.proceed(requestBuilder.build())
        } catch (t: Throwable) {
            throw IOException(t.message)
        }
        return response
    }
}