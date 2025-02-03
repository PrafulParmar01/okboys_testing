package com.ok.boys.delivery.base.api.home

import com.ok.boys.delivery.base.api.home.model.GetUsersByIdResponse
import com.ok.boys.delivery.base.api.home.model.UserDutyResponse
import io.reactivex.Single

class HomeRepository(
    private val homeRetrofitAPI: HomeRetrofitAPI
) {

    fun getUsersById(userId: String): Single<GetUsersByIdResponse> {
        return homeRetrofitAPI.getUsersById(userId)
    }
    fun setUserDUTYStatus(status: Boolean): Single<UserDutyResponse> {
        return homeRetrofitAPI.setUserDUTYStatus(status)
    }
}