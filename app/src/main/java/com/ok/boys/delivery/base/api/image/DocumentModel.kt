package com.ok.boys.delivery.base.api.image

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.base.api.orders.model.UploadInvoicePass


@Parcelize
data class DocumentModel(
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,
) : Parcelable


data class UploadInvoiceBox(
    var uploadInvoicePass: UploadInvoicePass? = null,
    var uploadInvoiceResponse: UploadImageResponse? = null,
)


@Parcelize
data class ExtraData(
    val any: String? = null
) : Parcelable


@Parcelize
data class GeneratePSURLResponse(
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,

    @field:SerializedName("response")
    val response: String? = null,

    @field:SerializedName("extraData")
    val extraData: ExtraData? = null,

    @field:SerializedName("errorResponse")
    val errorResponse: String? = null,

    ) : Parcelable