package com.ok.boys.delivery.ui.chat.viewmodel

import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.chat.ChatRepository
import com.ok.boys.delivery.base.api.chat.model.AddCommentResponse
import com.ok.boys.delivery.base.api.chat.model.GetCommentsResponse
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.api.orders.model.OrdersProcessResponse
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class ChatViewModel(private val chatRepository: ChatRepository) :
    BaseViewModel() {

    private val addChatViewStateSubject: BehaviorSubject<ChatViewState> = BehaviorSubject.create()
    val addChatViewState: Observable<ChatViewState> = addChatViewStateSubject.hide()

    private val getChatViewStateSubject: BehaviorSubject<ChatViewState> = BehaviorSubject.create()
    val getChatViewState: Observable<ChatViewState> = getChatViewStateSubject.hide()

    fun getAddComment(orderId: String?,message: String) {
        chatRepository.getAddComment(orderId,message)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                addChatViewStateSubject.onNext(ChatViewState.LoadingState(true))
            }.doAfterTerminate {
                addChatViewStateSubject.onNext(ChatViewState.LoadingState(false))
            }
            .subscribe({
                addChatViewStateSubject.onNext(ChatViewState.SuccessAddCommentsResponse(it))
            }, {
                addChatViewStateSubject.onNext(ChatViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }
    fun getCommentsList(orderId :String?) {
        chatRepository.getCommentsList(orderId)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                getChatViewStateSubject.onNext(ChatViewState.LoadingState(true))
            }.doAfterTerminate {
                getChatViewStateSubject.onNext(ChatViewState.LoadingState(false))
            }
            .subscribe({
                getChatViewStateSubject.onNext(ChatViewState.SuccessGetCommentsResponse(it))
            }, {
                getChatViewStateSubject.onNext(ChatViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }
}

sealed class ChatViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : ChatViewState()
    data class SuccessGetCommentsResponse(val successMessage: GetCommentsResponse) : ChatViewState()
    data class SuccessAddCommentsResponse(val successMessage: AddCommentResponse) : ChatViewState()
    data class LoadingState(val isLoading: Boolean) : ChatViewState()
}


sealed class AcceptRejectViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : AcceptRejectViewState()
    data class SuccessResponse(val successMessage: OrdersProcessResponse) : AcceptRejectViewState()
    data class LoadingState(val isLoading: Boolean) : AcceptRejectViewState()
}

