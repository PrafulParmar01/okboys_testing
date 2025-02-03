package com.ok.boys.delivery.services

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class NotificationResponse(


    @field:SerializedName("notification")
    var notification: NotificationPayload? = null,

    @field:SerializedName("data")
    var data: DataPayload? = null,


    ) : Parcelable

@Parcelize
data class NotificationPayload(

    @field:SerializedName("title")
    var title: String = "",

    @field:SerializedName("body")
    var body: String = "",

    ) : Parcelable


@Parcelize
data class DataPayload(

    @field:SerializedName("title")
    var title: String = "",

    @field:SerializedName("body")
    var body: String = "",

    @field:SerializedName("orderId")
    var orderId: String = "",

    @field:SerializedName("jobId")
    var jobId: String = "",

    @field:SerializedName("type")
    var type: String = "",

    @field:SerializedName("userId")
    var userId: String = "",

    @field:SerializedName("email")
    var email: String = "",

    @field:SerializedName("orderState")
    var orderState: String = "",

    @field:SerializedName("jobState")
    var jobState: String = "",
) : Parcelable