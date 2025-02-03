package com.ok.boys.delivery.base.api.login

import com.ok.boys.delivery.base.api.login.model.GenerateOTPRequest
import com.ok.boys.delivery.base.api.login.model.GenerateOTPResponse
import com.ok.boys.delivery.base.api.login.model.VerifyOTPRequest
import com.ok.boys.delivery.base.api.network.ErrorType
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import retrofit2.http.GET
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginRetrofitAPI {

    @Headers("apikey: 4CD8bMhwyfmv29OxF4pNRr4WtjwXFDrB")
    @POST(ApiConstant.API_GENERATE_OTP)
    fun getGenerateOTP(@Body generateOTPRequest: GenerateOTPRequest): Single<GenerateOTPResponse>

    @Headers("apikey: 4CD8bMhwyfmv29OxF4pNRr4WtjwXFDrB")
    @POST(ApiConstant.API_VERIFY_OTP)
    fun getVerifyOTP(@Body verifyOTPRequest: VerifyOTPRequest): Single<Response<ResponseBody>>
}