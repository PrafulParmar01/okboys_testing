package com.ok.boys.delivery.base.api.chat.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ChatModel(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,


) : Parcelable

@Parcelize
data class ChatResponse(
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
data class AddCommentResponse(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("extraData")
	val extraData: ExtraData? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	@field:SerializedName("response")
	val response: String? = null,

	) : Parcelable
@Parcelize

data class GetCommentsResponse(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("extraData")
	val extraData: ExtraData? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

	@field:SerializedName("response")
	val commentsModelList: List<CommentsModel> = listOf(),

	) : Parcelable

@Parcelize
data class CommentsModel(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("createdTs")
	val createdTs: String? = null,

	@field:SerializedName("updatedBy")
	val updatedBy: String? = null,

	@field:SerializedName("updatedTs")
	val updatedTs: String? = null,

	@field:SerializedName("orderId")
	val orderId: String? = null,

	@field:SerializedName("convType")
	val convType: String? = null,

	@field:SerializedName("attachmentLink")
	val attachmentLink: String? = null,

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("seenbyuser")
	val seenbyuser: String? = null,

	@field:SerializedName("fileS3Path")
	val fileS3Path: String? = null,
	) : Parcelable


@Parcelize
data class AddCommentRequest(

	@field:SerializedName("latitude")
	var latitude: Double? = null,

	@field:SerializedName("longitude")
	var longitude: Double? = null,

	@field:SerializedName("altitude")
	var altitude: Int? = null,

	@field:SerializedName("accuracy")
	var accuracy: Int? = null,

	@field:SerializedName("deviceTime")
	var deviceTime: String? = null,

	@field:SerializedName("fixTime")
	var fixTime: String? = null,

	@field:SerializedName("isValid")
	var isValid: Boolean? = null,

	@field:SerializedName("course")
	var course: Int? = null,

	@field:SerializedName("speed")
	var speed: Int? = null,

) : Parcelable



@Parcelize
data class UploadImageResponse(
	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("response")
	val response: UploadInvoiceItemResponse? = null,

	@field:SerializedName("extraData")
	val extraData: ExtraData? = null,

	@field:SerializedName("errorResponse")
	val errorResponse: String? = null,

) : Parcelable


@Parcelize
data class UploadInvoiceItemResponse(
	@field:SerializedName("psUrl")
	val psUrl: String? = null,

	@field:SerializedName("filePath")
	val filePath: String = "",

) : Parcelable
