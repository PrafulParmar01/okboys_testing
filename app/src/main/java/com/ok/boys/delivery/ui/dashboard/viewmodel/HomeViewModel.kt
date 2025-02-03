package com.ok.boys.delivery.ui.dashboard.viewmodel

import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.home.HomeRepository
import com.ok.boys.delivery.base.api.home.model.GetUsersByIdResponse
import com.ok.boys.delivery.base.api.home.model.UserDutyResponse
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewState
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.ApiConstant.TAKE_UNTIL_DELAY
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class HomeViewModel(private val homeRepository: HomeRepository) : BaseViewModel() {

    private val homeViewStateSubject: BehaviorSubject<HomeViewState> = BehaviorSubject.create()
    val homeViewState: Observable<HomeViewState> = homeViewStateSubject.hide()

    fun getUsersById(userId:String) {
        homeRepository.getUsersById(userId)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                homeViewStateSubject.onNext(HomeViewState.LoadingState(true))
            }.doAfterTerminate {
                homeViewStateSubject.onNext(HomeViewState.LoadingState(false))
            }
            .subscribe({
                homeViewStateSubject.onNext(HomeViewState.SuccessResponse(it))
            }, {
                homeViewStateSubject.onNext(HomeViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()

    }

    fun setUserDutyStatus(status:Boolean) {
        homeRepository.setUserDUTYStatus(status)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                homeViewStateSubject.onNext(HomeViewState.LoadingState(true))
            }.doAfterTerminate {
                homeViewStateSubject.onNext(HomeViewState.LoadingState(false))
            }
            .subscribe({
                homeViewStateSubject.onNext(HomeViewState.UserDutyStatusResponse(it))
            }, {
                homeViewStateSubject.onNext(HomeViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()

    }
}

sealed class HomeViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : HomeViewState()
    data class SuccessResponse(val successMessage: GetUsersByIdResponse) : HomeViewState()
    data class UserDutyStatusResponse(val statusMessage: UserDutyResponse) : HomeViewState()
    data class LoadingState(val isLoading: Boolean) : HomeViewState()
}

