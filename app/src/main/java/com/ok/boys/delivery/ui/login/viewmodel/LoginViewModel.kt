package com.ok.boys.delivery.ui.login.viewmodel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.login.LoginRepository
import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse
import com.ok.boys.delivery.base.api.login.model.GenerateOTPRequest
import com.ok.boys.delivery.base.api.login.model.GenerateOTPResponse
import com.ok.boys.delivery.base.api.login.model.VerifyOTPRequest
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class LoginViewModel(private val loginRepository: LoginRepository) :
    BaseViewModel() {

    private val otpStateSubject: BehaviorSubject<OTPViewState> = BehaviorSubject.create()
    val otpViewState: Observable<OTPViewState> = otpStateSubject.hide()

    private val loginStateSubject: BehaviorSubject<LoginViewState> = BehaviorSubject.create()
    val loginViewState: Observable<LoginViewState> = loginStateSubject.hide()

    fun getGenerateOTP(generateOTPRequest: GenerateOTPRequest) {
        loginRepository.getGenerateOTP(generateOTPRequest)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                otpStateSubject.onNext(OTPViewState.LoadingState(true))
            }.doAfterTerminate {
                otpStateSubject.onNext(OTPViewState.LoadingState(false))
            }
            .subscribe({
                otpStateSubject.onNext(OTPViewState.SuccessResponse(it))
            }, {
                otpStateSubject.onNext(OTPViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }



    fun getVerifyOTP(verifyOTPRequest: VerifyOTPRequest) {
        loginRepository.getVerifyOTP(verifyOTPRequest)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                loginStateSubject.onNext(LoginViewState.LoadingState(true))
            }.doAfterTerminate {
                loginStateSubject.onNext(LoginViewState.LoadingState(false))
            }
            .subscribe({
                val tokenType = object : TypeToken<GenerateLoginResponse>() {}.type
                val responseString = it.body()?.string()
                val fromJson = Gson().fromJson<GenerateLoginResponse>(responseString, tokenType)
                loginStateSubject.onNext(LoginViewState.SuccessResponse(fromJson))
            }, {
                loginStateSubject.onNext(LoginViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }
}

sealed class OTPViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : OTPViewState()
    data class SuccessResponse(val successMessage: GenerateOTPResponse) : OTPViewState()
    data class LoadingState(val isLoading: Boolean) : OTPViewState()
}


sealed class LoginViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : LoginViewState()
    data class SuccessResponse(val successMessage: GenerateLoginResponse) : LoginViewState()
    data class LoadingState(val isLoading: Boolean) : LoginViewState()
}


