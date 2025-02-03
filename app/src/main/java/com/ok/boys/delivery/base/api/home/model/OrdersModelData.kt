package com.ok.boys.delivery.base.api.home.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class OrdersModelData(

	@field:SerializedName("id")
	val id: Int? = null,

	var isExpanded: Boolean? = false,


	) : Parcelable
