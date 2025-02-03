package com.ok.boys.delivery.base.api.orders

import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.base.api.orders.model.*
import com.ok.boys.delivery.ui.payment.GenerateOrderRequest
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*


interface OrdersRetrofitAPI {

    @POST(ApiConstant.API_GET_ORDER_LIST)
    fun getOrderList(@Body request: OrderListRequest): Single<OrdersResponse>

    @GET(ApiConstant.API_GET_ORDER_ACCEPTED_LIST)
    fun getOrderAcceptedList(): Single<OrdersAcceptResponse>

    @PUT(ApiConstant.API_ORDER_ACCEPT_REJECT)
    fun getOrderAcceptRejectAPI(@Query("orderId") orderId: String?,@Body request: OrderAcceptRejectRequest): Single<OrdersProcessResponse>

    @PUT(ApiConstant.API_PAYMENT_REQUEST)
    fun getPaymentRequest(@Query("orderId") orderId: String?,@Body request: OrderPaymentRequest): Single<OrdersProcessResponse>

    @PUT(ApiConstant.API_PAYMENT_CONFIRMED)
    fun getPaymentConfirmRequest(@Query("orderId") orderId: String?,@Body request: OrderPaymentRequest): Single<OrdersProcessResponse>

    @PUT(ApiConstant.API_ORDER_DELIVERED_EVENT)
    fun getOrderDeliveredRequest(@Query("orderId") orderId: String?,@Body request: OrderPaymentRequest): Single<OrdersProcessResponse>


    @PUT(ApiConstant.API_PAYMENT_REQUEST)
    fun getOrderPaymentFeesRequest(@Query("orderId") orderId: String?,@Body request: OrderPaymentFeesRequest): Single<OrdersProcessResponse>

    @PUT(ApiConstant.API_UPLOAD_INVOICE_IMAGE_EVENT)
    fun getUploadInvoiceEvent(
        @Query("orderId") orderId: String,
        @Body request: UploadInvoiceRequest
    ): Single<OrdersProcessResponse>


    @GET(ApiConstant.API_PREFERENCES)
    fun getPreferences(): Single<CategoryResponse>

    @PUT(ApiConstant.API_GENERATE_ORDER_REQUEST)
    fun generateOrderRequest(@Query("orderId") orderId: String?,@Body request: GenerateOrderRequest): Single<OrdersProcessResponse>

}