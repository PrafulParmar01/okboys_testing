package com.ok.boys.delivery.base.api.history

import com.ok.boys.delivery.base.api.orders.model.*
import io.reactivex.Single

class HistoryRepository(
    private val historyRetrofitAPI: HistoryRetrofitAPI
) {

    fun getHistoryList(request: OrderListRequest): Single<OrdersResponse> {
        return historyRetrofitAPI.getHistoryList(request)
    }

}