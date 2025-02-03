package com.ok.boys.delivery.base.api.orders.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class CategoryResponse(

	@field:SerializedName("pagination")
	val pagination: PaginationModel? = null,

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
data class CategoryItem(

	@field:SerializedName("created_ts")
	val createdTs: Long? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null
) : Parcelable



@Parcelize
data class Response(

	@field:SerializedName("category")
	val category: List<CategoryItem> = listOf(),

	@field:SerializedName("clusters")
	val clusters: List<ClustersItem?>? = null,

	@field:SerializedName("states")
	val states: List<StatesItem?>? = null
) : Parcelable

@Parcelize
data class StatesItem(

	@field:SerializedName("next_state")
	val nextState: String? = null,

	@field:SerializedName("available_states")
	val availableStates: String? = null,

	@field:SerializedName("requested_state")
	val requestedState: String? = null,

	@field:SerializedName("event_type")
	val eventType: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null
) : Parcelable

@Parcelize
data class ClustersItem(

	@field:SerializedName("cluster_id")
	val clusterId: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("created_ts")
	val createdTs: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null
) : Parcelable
