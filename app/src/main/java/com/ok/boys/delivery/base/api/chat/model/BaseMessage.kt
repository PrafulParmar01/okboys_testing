package com.ok.boys.delivery.base.api.chat.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ok.boys.delivery.util.ApiConstant
import kotlinx.parcelize.Parcelize


@Parcelize
data class BaseMessage(


	@field:SerializedName("sender")
	var isSender: Int? = null,

	@field:SerializedName("message")
	var message: CommentsModel? = null,


) : Parcelable
