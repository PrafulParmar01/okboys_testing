package com.ok.boys.delivery.base.api.history

import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrdersResponse
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST


interface HistoryRetrofitAPI {

    @POST(ApiConstant.API_ORDER_HISTORY)
    fun getHistoryList(@Body request: OrderListRequest): Single<OrdersResponse>
}