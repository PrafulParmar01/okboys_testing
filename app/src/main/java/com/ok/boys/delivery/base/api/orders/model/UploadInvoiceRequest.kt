package com.ok.boys.delivery.base.api.orders.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class UploadInvoiceRequest(

	@field:SerializedName("orderJobId")
	val orderJobId: String? = null,

	@field:SerializedName("jobInvoice")
	val jobInvoice: JobInvoice? = null,

	@field:SerializedName("event")
	val event: String? = null
) : Parcelable


@Parcelize
data class JobInvoice(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("jobId")
	val jobId: String? = null,

	@field:SerializedName("invoice1mage1")
	val invoice1mage1: String? = null,

	@field:SerializedName("invoice1mage2")
	val invoice1mage2: String? = null,

	@field:SerializedName("itemTotalAmount")
	val itemTotalAmount: Double? = null,

) : Parcelable
