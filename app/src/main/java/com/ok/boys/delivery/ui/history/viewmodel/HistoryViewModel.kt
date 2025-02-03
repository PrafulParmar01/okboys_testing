package com.ok.boys.delivery.ui.history.viewmodel

import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.history.HistoryRepository
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrdersResponse
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


class HistoryViewModel(private val historyRepository: HistoryRepository) : BaseViewModel() {

    private val historyViewStateSubject: BehaviorSubject<HistoryViewState> = BehaviorSubject.create()
    val historyViewState: Observable<HistoryViewState> = historyViewStateSubject.hide()

    fun getHistoryList(request: OrderListRequest) {
        historyRepository.getHistoryList(request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                historyViewStateSubject.onNext(HistoryViewState.LoadingState(true))
            }.doAfterTerminate {
                historyViewStateSubject.onNext(HistoryViewState.LoadingState(false))
            }
            .subscribe({
                historyViewStateSubject.onNext(HistoryViewState.SuccessResponse(it))
            }, {
                historyViewStateSubject.onNext(HistoryViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }

}

sealed class HistoryViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : HistoryViewState()
    data class SuccessResponse(val successMessage: OrdersResponse) : HistoryViewState()
    data class LoadingState(val isLoading: Boolean) : HistoryViewState()
}

