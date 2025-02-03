package com.ok.boys.delivery.base.api.home

import com.ok.boys.delivery.base.api.home.model.GetUsersByIdResponse
import com.ok.boys.delivery.base.api.home.model.UserDutyResponse
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface HomeRetrofitAPI {

    @GET(ApiConstant.API_GET_USER_BY_ID)
    fun getUsersById(@Path("id") id : String): Single<GetUsersByIdResponse>

   @PUT(ApiConstant.API_USER_DUTY_STATUS)
    fun setUserDUTYStatus(@Query("status") status: Boolean): Single<UserDutyResponse>


}