package com.ok.boys.delivery.base.api.orders.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class OrdersResponse(
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,

    @field:SerializedName("response")
    val responseList: List<OrderResponse>? = null,

    @field:SerializedName("pagination")
    val pagination: PaginationModel? = null,

    @field:SerializedName("extraData")
    val extraData: ExtraData? = null,

    @field:SerializedName("errorResponse")
    val errorResponse: String? = null,

    ) : Parcelable

@Parcelize
data class OrdersAcceptResponse(
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,

    @field:SerializedName("response")
    val responseList: OrderResponse? = null,

    @field:SerializedName("pagination")
    val pagination: PaginationModel? = null,

    @field:SerializedName("extraData")
    val extraData: ExtraData? = null,

    @field:SerializedName("errorResponse")
    val errorResponse: String? = null,

    ) : Parcelable

@Parcelize
data class ExtraData(
    val any: String? = null
) : Parcelable

@Parcelize
data class OrderResponse(

    @field:SerializedName("userAddress")
    val userAddress: UserAddressItem? = null,

    @field:SerializedName("orderNumber")
    val orderNumber: String? = null,

    @field:SerializedName("deliveryAmount")
    val deliveryAmount: Double = 0.0,

    @field:SerializedName("totalDistance")
    val totalDistance: Double? = null,

    @field:SerializedName("availableStates")
    val availableStates: String? = null,

    @field:SerializedName("id")
    val id: String = "",

    @field:SerializedName("createdTs")
    val createdTs: Long = 0L,

    @field:SerializedName("deliveryBoyId")
    val deliveryBoyId: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("orderState")
    val orderState: String? = null,

    @field:SerializedName("jobAddress")
    val jobAddress: MutableList<JobAddressItem> = mutableListOf(),

    @field:SerializedName("customer")
    val customer: CustomerModel? = null,

    var isExpanded: Boolean = false,

    ) : Parcelable

@Parcelize
data class UserAddressItem(

    var orderId: String = "",

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("area")
    val area: String? = null,

    @field:SerializedName("pincode")
    val pincode: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("lng")
    val lng: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("addressType")
    val addressType: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    var selected: Boolean = false,

    @field:SerializedName("availableStates")
    val availableStates: String? = null,

    @field:SerializedName("orderState")
    val orderState: String? = null,

    var isCompleted: Boolean = false


) : Parcelable

@Parcelize
data class JobAddressItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("area")
    val area: String? = null,

    @field:SerializedName("pincode")
    val pincode: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("lng")
    val lng: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("addressType")
    val addressType: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("orderJobId")
    val orderJobId: String? = null,

    @field:SerializedName("workflowState")
    val workflowState: String? = null,

    @field:SerializedName("categoryId")
    val categoryId: String? = null,

    @field:SerializedName("sequance")
    val sequance: Int = 0,

    @field:SerializedName("invoiceDetails")
    val invoiceDetails: List<InvoiceDetails> = listOf(),

    @field:SerializedName("paymentDetails")
    val paymentDetails: List<PaymentDetails> = listOf(),

    var selected: Boolean = false,
    var isCompleted: Boolean = false
) : Parcelable

@Parcelize
data class InvoiceDetails(

    @field:SerializedName("invoiceId")
    var invoiceId: String? = null,

    @field:SerializedName("invoiceJobId")
    var invoiceJobId: String? = null,

    @field:SerializedName("invoiceImage1")
    var invoiceImage1: String? = null,

    @field:SerializedName("invoiceImage2")
    var invoiceImage2: String? = null,

    @field:SerializedName("invoiceTotalAmount")
    var invoiceTotalAmount: String? = null,

    @field:SerializedName("psUrl1")
    var psUrl1: String = "",

    @field:SerializedName("psUrl2")
    var psUrl2: String = "",

    ) : Parcelable

@Parcelize
data class PaymentDetails(

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("transactionMode")
    var transactionMode: String? = null,

    @field:SerializedName("transactionId")
    var transactionId: String? = null,

    @field:SerializedName("transactionStatus")
    var transactionStatus: String? = null,

    @field:SerializedName("amount")
    var amount: Double? = null,

    @field:SerializedName("upiHandle")
    var upiHandle: String? = null,

    @field:SerializedName("upiImage")
    var upiImage: String? = null,

    @field:SerializedName("jobPaymentStatus")
    var jobPaymentStatus: String? = null,

    @field:SerializedName("createdBy")
    var createdBy: String? = null,

    @field:SerializedName("createdTs")
    val createdTs: Long = 0L,

    @field:SerializedName("updatedBy")
    var updatedBy: String? = null,

    @field:SerializedName("updatedTs")
    val updatedTs: Long = 0L,


    ) : Parcelable

@Parcelize
data class CustomerModel(

    @field:SerializedName("mobileNumber")
    var mobileNumber: String? = null,

    @field:SerializedName("userName")
    var userName: String? = null,

    ) : Parcelable

@Parcelize
data class PaginationModel(

    @field:SerializedName("totalRecords")
    var totalRecords: Int? = null,

    @field:SerializedName("totalPages")
    var totalPages: Int? = null,

    @field:SerializedName("noOfRecordsPerPage")
    var noOfRecordsPerPage: Int? = null,

    @field:SerializedName("pageNumber")
    var pageNumber: Int? = null,

    ) : Parcelable


@Parcelize
data class OrderListRequest(

    @field:SerializedName("pageNumber")
    var pageNumber: Int? = null,

    @field:SerializedName("noOfRecordsPerPage")
    var noOfRecordsPerPage: Int? = null,

    ) : Parcelable

@Parcelize
data class OrderAcceptRejectRequest(

    @field:SerializedName("event")
    var event: String? = null,


    ) : Parcelable

@Parcelize
data class OrderPaymentRequest(

    @field:SerializedName("event")
    var event: String? = null,

    @field:SerializedName("orderJobId")
    var orderJobId: String? = null,

    @field:SerializedName("jobPayment")
    var jobPayment: JobPaymentRequest? = null,

    ) : Parcelable


@Parcelize
data class JobPaymentRequest(

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("amount")
    var amount: Double? = null,

    @field:SerializedName("upiHandle")
    var upiHandle: String? = null,

    @field:SerializedName("upiImage")
    var upiImage: String? = null,

    @field:SerializedName("transactionMode")
    var transactionMode: String? = null,
) : Parcelable


@Parcelize
data class OrdersProcessResponse(
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,

    @field:SerializedName("response")
    val response: String? = null,

    @field:SerializedName("errorLevel")
    val errorLevel: String? = null,

    @field:SerializedName("errorResponse")
    val errorResponse: String? = null,

    ) : Parcelable


// PAY SERVICE FEES...

@Parcelize
data class OrderPaymentFeesRequest(

    @field:SerializedName("event")
    var event: String? = null,

    @field:SerializedName("orderProcessFee")
    var orderProcessFee: PaymentProcessFeesRequest? = null,

    ) : Parcelable


@Parcelize
data class PaymentProcessFeesRequest(

    @field:SerializedName("transactionId")
    var transactionId: String? = null,

    @field:SerializedName("transactionMode")
    var transactionMode: String? = null,

    @field:SerializedName("transactionStatus")
    var transactionStatus: String? = null,

    @field:SerializedName("upiHandle")
    var upiHandle: String? = null,

    @field:SerializedName("amount")
    var amount: Int? = null,
) : Parcelable
