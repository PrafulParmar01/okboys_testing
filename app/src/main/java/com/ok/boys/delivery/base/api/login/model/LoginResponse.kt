package com.ok.boys.delivery.base.api.login.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName



@Entity
@Parcelize
data class GenerateLoginResponse(

    @PrimaryKey
    @field:SerializedName("statusCode")
    val statusCode: Int? = null,

    @field:SerializedName("response")
    val response: LoginDataResponse? = null,

    @field:SerializedName("errorResponse")
    val errorResponse: String? = null,

    @field:SerializedName("errorLevel")
    val errorLevel: String? = null

) : Parcelable


@Entity
@Parcelize
data class LoginDataResponse(

   /* @field:SerializedName("userAddress")
    val userAddress: List<String>? = null,
*/

    @field:SerializedName("loginResponse")
    val loginResponse: LoginResponse? = null,

    @field:SerializedName("order")
    val orderResponse: LoginOrderResponse? = null,

    @field:SerializedName("userStatus")
    val userStatus: UserStatus? = null,

    @field:SerializedName("userDetails")
    val userDetails: UserDetails? = null,

    @field:SerializedName("franchiseDetails")
    val franchiseDetails: FranchiseDetails? = null
) : Parcelable


@Entity
@Parcelize
data class LoginResponse(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("roles")
    val roles: List<String?>? = null,


    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("refresh_token")
    val refreshToken: String? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("expires_in")
    val expiresIn: Int? = null,

    @field:SerializedName("refresh_expires_in")
    val refreshExpiresIn: Int? = null,

) : Parcelable

@Entity
@Parcelize
data class LoginOrderResponse(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("createdTs")
    val createdTs:  String? = null,


    @field:SerializedName("updatedBy")
    val updatedBy: String? = null,

    @field:SerializedName("updatedTs")
    val updatedTs: String? = null,

    @field:SerializedName("orderNumber")
    val orderNumber: String? = null,

    @field:SerializedName("deliveryAddressId")
    val deliveryAddressId: String? = null,

    @field:SerializedName("orderState")
    val orderState: String? = null,

    @field:SerializedName("deliveryBoyId")
    val deliveryBoyId: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("deliveryAmount")
    val deliveryAmount: Double? = null,

    @field:SerializedName("active")
    val active: Boolean? = null,

    ) : Parcelable


@Entity
@Parcelize
data class UserStatus(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("createdTs")
    val createdTs: Long? = null,

    @field:SerializedName("updatedBy")
    val updatedBy: String? = null,

    @field:SerializedName("updatedTs")
    val updatedTs: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("inDuty")
    val inDuty: Boolean? = null,


) : Parcelable

@Entity
@Parcelize
data class UserDetails(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("createdTs")
    val createdTs: Long? = null,

    @field:SerializedName("updatedBy")
    val updatedBy: String? = null,

    @field:SerializedName("updatedTs")
    val updatedTs: String? = null,

    @field:SerializedName("mobileNumber")
    val mobileNumber: String? = null,

    @field:SerializedName("authType")
    val authType: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("userType")
    val userType: String? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("franchiseId")
    val franchiseId: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("active")
    val active: Boolean? = null,


) : Parcelable

@Entity
@Parcelize
data class FranchiseDetails(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("createdTs")
    val createdTs: Long? = null,

    @field:SerializedName("updatedBy")
    val updatedBy: String? = null,

    @field:SerializedName("updatedTs")
    val updatedTs: String? = null,

    @field:SerializedName("franchiseName")
    val franchiseName: String? = null,

    @field:SerializedName("gstNo")
    val gstNo: String? = null,

    @field:SerializedName("mobileNumber")
    val mobileNumber: String? = null,


    @field:SerializedName("clusterId")
    val clusterId: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,


    @field:SerializedName("franchiseType")
    val franchiseType: String? = null,

    @field:SerializedName("email")
    val email: String? = null,


    @field:SerializedName("active")
    val active: Boolean? = null,


) : Parcelable


