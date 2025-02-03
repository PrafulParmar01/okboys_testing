package com.ok.boys.delivery.ui.orders.viewmodel

import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.api.orders.OrdersRepository
import com.ok.boys.delivery.base.api.orders.model.CategoryResponse
import com.ok.boys.delivery.base.api.orders.model.OrderAcceptRejectRequest
import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentFeesRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.OrdersAcceptResponse
import com.ok.boys.delivery.base.api.orders.model.OrdersProcessResponse
import com.ok.boys.delivery.base.api.orders.model.OrdersResponse
import com.ok.boys.delivery.base.api.orders.model.UploadInvoiceRequest
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.ui.payment.GenerateOrderRequest
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class OrderViewModel(private val orderRepository: OrdersRepository) :
    BaseViewModel() {

    private val orderViewStateSubject: PublishSubject<OrderViewState> = PublishSubject.create()
    val orderViewState: Observable<OrderViewState> = orderViewStateSubject.hide()

    private val orderAcceptedViewStateSubject: PublishSubject<OrderAcceptedViewState> =
        PublishSubject.create()
    val orderAcceptedViewState: Observable<OrderAcceptedViewState> =
        orderAcceptedViewStateSubject.hide()


    private val acceptRejectViewStateSubject: PublishSubject<AcceptRejectViewState> =
        PublishSubject.create()
    val acceptRejectViewState: Observable<AcceptRejectViewState> =
        acceptRejectViewStateSubject.hide()


    private val orderCategoryViewStateSubject: PublishSubject<OrderCategoryViewState> =
        PublishSubject.create()
    val orderCategoryViewState: Observable<OrderCategoryViewState> =
        orderCategoryViewStateSubject.hide()


    private val orderProcessViewStateSubject: PublishSubject<OrderProcessViewState> =
        PublishSubject.create()
    val orderProcessViewState: Observable<OrderProcessViewState> =
        orderProcessViewStateSubject.hide()



    private val uploadPaymentImageViewStateSubject: PublishSubject<UploadPaymentViewState> = PublishSubject.create()
    val uploadPaymentImageViewState: Observable<UploadPaymentViewState> = uploadPaymentImageViewStateSubject.hide()


    private val generateOrderStateSubject: PublishSubject<GenerateOrderProcessViewState> = PublishSubject.create()
    val generateOrderProcessViewState: Observable<GenerateOrderProcessViewState> = generateOrderStateSubject.hide()



    fun getOrderList(request: OrderListRequest) {
        orderRepository.getOrderList(request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderViewStateSubject.onNext(OrderViewState.LoadingState(true))
            }.doAfterTerminate {
                orderViewStateSubject.onNext(OrderViewState.LoadingState(false))
            }
            .subscribe({
                orderViewStateSubject.onNext(OrderViewState.SuccessResponse(it))
            }, {
                orderViewStateSubject.onNext(OrderViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }

    fun getOrderAcceptedList() {
        orderRepository.getOrderAcceptedList()
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderAcceptedViewStateSubject.onNext(OrderAcceptedViewState.LoadingState(true))
            }.doAfterTerminate {
                orderAcceptedViewStateSubject.onNext(OrderAcceptedViewState.LoadingState(false))
            }
            .subscribe({
                orderAcceptedViewStateSubject.onNext(OrderAcceptedViewState.SuccessResponse(it))
            }, {
                orderAcceptedViewStateSubject.onNext(
                    OrderAcceptedViewState.ErrorMessage(
                        onHandleError(it)
                    )
                )
            }).autoDispose()
    }

    fun getOrderAcceptRejectAPI(orderId: String?, request: OrderAcceptRejectRequest) {
        orderRepository.getOrderAcceptRejectAPI(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                acceptRejectViewStateSubject.onNext(AcceptRejectViewState.LoadingState(true))
            }.doAfterTerminate {
                acceptRejectViewStateSubject.onNext(AcceptRejectViewState.LoadingState(false))
            }
            .subscribe({
                acceptRejectViewStateSubject.onNext(AcceptRejectViewState.SuccessResponse(it))
            }, {
                acceptRejectViewStateSubject.onNext(
                    AcceptRejectViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }

    fun getOrderCategory() {
        orderRepository.getPreferences()
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderCategoryViewStateSubject.onNext(OrderCategoryViewState.LoadingState(true))
            }.doAfterTerminate {
                orderCategoryViewStateSubject.onNext(OrderCategoryViewState.LoadingState(false))
            }
            .subscribe({
                orderCategoryViewStateSubject.onNext(OrderCategoryViewState.SuccessResponse(it))
            }, {
                orderCategoryViewStateSubject.onNext(
                    OrderCategoryViewState.ErrorMessage(
                        onHandleError(it)
                    )
                )
            }).autoDispose()
    }


    fun getOrderProcessAPI(orderId: String?, request: OrderPaymentRequest) {
        orderRepository.getPaymentRequest(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                orderProcessViewStateSubject.onNext(OrderProcessViewState.SuccessResponse(it))
            }, {
                orderProcessViewStateSubject.onNext(
                    OrderProcessViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }


    fun getPaymentConfirmRequest(orderId: String?, request: OrderPaymentRequest) {
        orderRepository.getPaymentConfirmRequest(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                orderProcessViewStateSubject.onNext(OrderProcessViewState.SuccessResponse(it))
            }, {
                orderProcessViewStateSubject.onNext(
                    OrderProcessViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }


    fun getOrderDeliveredRequest(orderId: String?, request: OrderPaymentRequest) {
        orderRepository.getOrderDeliveredRequest(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                orderProcessViewStateSubject.onNext(OrderProcessViewState.SuccessResponse(it))
            }, {
                orderProcessViewStateSubject.onNext(
                    OrderProcessViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }

    fun getUploadInvoiceEvent(orderId: String, uploadInvoiceRequest: UploadInvoiceRequest) {
        orderRepository.getUploadInvoiceEvent(orderId, uploadInvoiceRequest)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                orderProcessViewStateSubject.onNext(OrderProcessViewState.SuccessResponse(it))
            }, {
                orderProcessViewStateSubject.onNext(
                    OrderProcessViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }


    fun getOrderPaymentFeesRequest(orderId: String?, request: OrderPaymentFeesRequest) {
        orderRepository.getOrderPaymentFeesRequest(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                orderProcessViewStateSubject.onNext(OrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                orderProcessViewStateSubject.onNext(OrderProcessViewState.SuccessResponse(it))
            }, {
                orderProcessViewStateSubject.onNext(
                    OrderProcessViewState.ErrorMessage(
                        onHandleError(
                            it
                        )
                    )
                )
            }).autoDispose()
    }


    fun generateOrderRequest(orderId: String?, request: GenerateOrderRequest) {
        orderRepository.generateOrderRequest(orderId, request)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                generateOrderStateSubject.onNext(GenerateOrderProcessViewState.LoadingState(true))
            }.doAfterTerminate {
                generateOrderStateSubject.onNext(GenerateOrderProcessViewState.LoadingState(false))
            }
            .subscribe({
                generateOrderStateSubject.onNext(GenerateOrderProcessViewState.SuccessResponse(it))
            }, {
                generateOrderStateSubject.onNext(GenerateOrderProcessViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }
}

sealed class OrderViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : OrderViewState()
    data class SuccessResponse(val successMessage: OrdersResponse) : OrderViewState()
    data class LoadingState(val isLoading: Boolean) : OrderViewState()
}


sealed class AcceptRejectViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : AcceptRejectViewState()
    data class SuccessResponse(val successMessage: OrdersProcessResponse) : AcceptRejectViewState()
    data class LoadingState(val isLoading: Boolean) : AcceptRejectViewState()
}


sealed class OrderAcceptedViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : OrderAcceptedViewState()
    data class SuccessResponse(val successMessage: OrdersAcceptResponse) : OrderAcceptedViewState()
    data class LoadingState(val isLoading: Boolean) : OrderAcceptedViewState()
}


sealed class OrderProcessViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : OrderProcessViewState()
    data class SuccessResponse(val successMessage: OrdersProcessResponse) : OrderProcessViewState()
    data class LoadingState(val isLoading: Boolean) : OrderProcessViewState()
}


sealed class OrderCategoryViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : OrderCategoryViewState()
    data class SuccessResponse(val successMessage: CategoryResponse) : OrderCategoryViewState()
    data class LoadingState(val isLoading: Boolean) : OrderCategoryViewState()
}


sealed class UploadPaymentViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : UploadPaymentViewState()
    data class SuccessResponse(val successMessage: UploadImageResponse) : UploadPaymentViewState()
    data class LoadingState(val isLoading: Boolean) : UploadPaymentViewState()
}


sealed class GenerateOrderProcessViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : GenerateOrderProcessViewState()
    data class SuccessResponse(val successMessage: OrdersProcessResponse) : GenerateOrderProcessViewState()
    data class LoadingState(val isLoading: Boolean) : GenerateOrderProcessViewState()
}