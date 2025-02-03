package com.ok.boys.delivery.base.api.tracking.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class TrackingUpdateResponse(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

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
data class UpdateTrackingPositionRequest(

	@field:SerializedName("latitude")
	var latitude: Double = 0.0,

	@field:SerializedName("longitude")
	var longitude: Double = 0.0,

	@field:SerializedName("altitude")
	var altitude: Int = 0,

	@field:SerializedName("accuracy")
	var accuracy: Int = 0,

	@field:SerializedName("deviceTime")
	var deviceTime: String = "",

	@field:SerializedName("fixTime")
	var fixTime: String = "",

	@field:SerializedName("isValid")
	var isValid: Boolean = true,

	@field:SerializedName("course")
	var course: Int = 0,

	@field:SerializedName("speed")
	var speed: Int = 0,

) : Parcelable



