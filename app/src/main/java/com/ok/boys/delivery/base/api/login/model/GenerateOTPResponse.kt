package com.ok.boys.delivery.base.api.login.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ok.boys.delivery.util.ApiConstant
import kotlinx.parcelize.Parcelize


@Parcelize
data class GenerateOTPResponse(

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	@field:SerializedName("errorLevel")
	val errorLevel: String? = null,


	@field:SerializedName("response")
	val response: String = "",


) : Parcelable


@Parcelize
data class GenerateOTPRequest(

	@field:SerializedName("mobileNumber")
	var mobileNumber: String? = null,

	@field:SerializedName("appType")
	val appType: String? = ApiConstant.APP_TYPE,

	) : Parcelable

@Parcelize
data class VerifyOTPRequest(

	@field:SerializedName("mobileNumber")
	var mobileNumber: String? = null,

	@field:SerializedName("appType")
	val appType: String? = ApiConstant.APP_TYPE,

	@field:SerializedName("otp")
	var otp: String? = null,

	@field:SerializedName("fcmId")
	var fcmId: String = "",

	) : Parcelable

@Parcelize
data class VerifyOTPResponse(

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	@field:SerializedName("errorLevel")
	val errorLevel: String? = null,


	@field:SerializedName("response")
	val response: Int? = null,


	) : Parcelable
