package com.ok.boys.delivery.base.api.orders.model

import android.widget.ImageView

data class UploadInvoicePass(
	var upiID: String =  "",
	val orderId: String = "",
	val deliveryAmount: Double? = null,
	val jobItem: JobAddressItem? = null,
	val imageView: ImageView? = null,
	val position: Int = 0,
)

