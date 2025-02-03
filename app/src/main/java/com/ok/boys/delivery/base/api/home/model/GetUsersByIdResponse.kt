package com.ok.boys.delivery.base.api.home.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ok.boys.delivery.base.api.login.model.LoginOrderResponse

@Parcelize
data class GetUsersByIdResponse(

	@field:SerializedName("pagination")
	val pagination: String? = null,

	@field:SerializedName("extraData")
	val extraData: ExtraData? = null,

	@field:SerializedName("response")
	val response: Response? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null
) : Parcelable

@Parcelize
data class ExtraData(
	val any: String? = null
) : Parcelable

@Parcelize
data class Response(

	@field:SerializedName("earnings")
	val earnings: List<EarningsItem> = listOf(),

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("mobileNumber")
	val mobileNumber: String? = null,

	@field:SerializedName("franchiseId")
	val franchiseId: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userType")
	val userType: String? = null,

	@field:SerializedName("authType")
	val authType: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("userName")
	val userName: String? = null,

	@field:SerializedName("onDuty")
	val onDuty: Boolean = false,

	@field:SerializedName("createdTs")
	val createdTs: Long? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("order")
	val orderResponse: LoginOrderResponse? = null
) : Parcelable

@Parcelize
data class EarningsItem(

	@field:SerializedName("amount")
	val amount: String = "",

	@field:SerializedName("month")
	val month: String = "",

	@field:SerializedName("year")
	val year: String = ""
) : Parcelable

@Parcelize
data class UserDutyResponse(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("response")
	val response: UserDutyResponseModel? = null,

	@field:SerializedName("errorLevel")
	val errorLevel: String? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	) : Parcelable

@Parcelize
data class UserDutyResponseModel(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("createdTs")
	val createdTs: Long? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("inDuty")
	val inDuty: Boolean = false,

	@field:SerializedName("updatedBy")
	val updatedBy: String? = null,

	@field:SerializedName("updatedTs")
	val updatedTs: Long? = null,

	) : Parcelable


