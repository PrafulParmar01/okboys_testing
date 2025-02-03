package com.ok.boys.delivery.base.api.login

import com.ok.boys.delivery.base.api.login.model.GenerateOTPRequest
import com.ok.boys.delivery.base.api.login.model.GenerateOTPResponse
import com.ok.boys.delivery.base.api.login.model.VerifyOTPRequest
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class LoginRepository(
    private val loginRetrofitAPI: LoginRetrofitAPI
) {

    fun getGenerateOTP(generateOTPRequest: GenerateOTPRequest): Single<GenerateOTPResponse> {
        return loginRetrofitAPI.getGenerateOTP(generateOTPRequest)
    }

    fun getVerifyOTP(verifyOTPRequest: VerifyOTPRequest): Single<Response<ResponseBody>> {
        return loginRetrofitAPI.getVerifyOTP(verifyOTPRequest)
    }
}