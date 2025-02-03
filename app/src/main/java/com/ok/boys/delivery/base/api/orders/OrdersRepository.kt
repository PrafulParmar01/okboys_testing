package com.ok.boys.delivery.base.api.orders

import com.ok.boys.delivery.base.api.orders.model.CategoryResponse
import com.ok.boys.delivery.base.api.orders.model.OrderAcceptRejectRequest
import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentFeesRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.OrdersAcceptResponse
import com.ok.boys.delivery.base.api.orders.model.OrdersProcessResponse
import com.ok.boys.delivery.base.api.orders.model.OrdersResponse
import com.ok.boys.delivery.base.api.orders.model.UploadInvoiceRequest
import com.ok.boys.delivery.ui.payment.GenerateOrderRequest
import io.reactivex.Single

class OrdersRepository(private val orderRetrofitAPI: OrdersRetrofitAPI) {

    fun getOrderList(request: OrderListRequest): Single<OrdersResponse> {
        return orderRetrofitAPI.getOrderList(request)
    }
    fun getOrderAcceptedList(): Single<OrdersAcceptResponse> {
        return orderRetrofitAPI.getOrderAcceptedList()
    }

    fun getOrderAcceptRejectAPI(orderId: String?, request: OrderAcceptRejectRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getOrderAcceptRejectAPI(orderId,request)
    }


    fun getPaymentRequest(orderId: String?, request: OrderPaymentRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getPaymentRequest(orderId,request)
    }

    fun getPaymentConfirmRequest(orderId: String?, request: OrderPaymentRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getPaymentConfirmRequest(orderId,request)
    }

    fun getOrderDeliveredRequest(orderId: String?, request: OrderPaymentRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getOrderDeliveredRequest(orderId,request)
    }

    fun getOrderPaymentFeesRequest(orderId: String?, request: OrderPaymentFeesRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getOrderPaymentFeesRequest(orderId,request)
    }


    fun getUploadInvoiceEvent(orderId: String, uploadInvoiceRequest: UploadInvoiceRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.getUploadInvoiceEvent(orderId,uploadInvoiceRequest)
    }

    fun getPreferences(): Single<CategoryResponse> {
        return orderRetrofitAPI.getPreferences()
    }

    fun generateOrderRequest(orderId: String?,request: GenerateOrderRequest): Single<OrdersProcessResponse> {
        return orderRetrofitAPI.generateOrderRequest(orderId,request)
    }
}