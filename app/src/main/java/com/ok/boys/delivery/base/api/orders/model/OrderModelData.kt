package com.ok.boys.delivery.base.api.orders.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class OrderModelData(

	@field:SerializedName("id")
	val id: Int? = null,

	var selected: Boolean? = false,


	) : Parcelable
