package com.ok.boys.delivery.base.api.chat

import com.ok.boys.delivery.base.api.chat.model.AddCommentResponse
import com.ok.boys.delivery.base.api.chat.model.GetCommentsResponse
import io.reactivex.Single


class ChatRepository(
    private val chatRetrofitAPI: ChatRetrofitAPI
) {

    fun getAddComment(orderId: String?,message: String): Single<AddCommentResponse> {
        return chatRetrofitAPI.getAddComment(orderId,message)
    }
    fun getCommentsList(orderId: String?): Single<GetCommentsResponse> {
        return chatRetrofitAPI.getCommentsList(orderId)
    }
}