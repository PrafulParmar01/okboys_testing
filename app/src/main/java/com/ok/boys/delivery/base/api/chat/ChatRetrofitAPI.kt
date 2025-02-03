package com.ok.boys.delivery.base.api.chat

import com.ok.boys.delivery.base.api.chat.model.AddCommentResponse
import com.ok.boys.delivery.base.api.chat.model.GetCommentsResponse
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import retrofit2.http.*


interface ChatRetrofitAPI {

    @POST(ApiConstant.API_ADD_COMMENT)
    fun getAddComment(
        @Query("orderId") orderId: String?,
        @Query("message") message: String
    ): Single<AddCommentResponse>

    @POST(ApiConstant.API_GET_COMMENT)
    fun getCommentsList(@Query("orderId") orderId: String?): Single<GetCommentsResponse>
}