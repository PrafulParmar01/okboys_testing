package com.ok.boys.delivery.ui.login.viewmodel

import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.local.LocalRepository
import com.ok.boys.delivery.base.api.login.model.GenerateLoginResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class LocalViewModel(private val localRepository: LocalRepository) : BaseViewModel() {

    private val localStateSubject: PublishSubject<LocalViewState> = PublishSubject.create()
    val localViewState: Observable<LocalViewState> = localStateSubject.hide()

    fun saveLoginInfo(loginDataResponse: GenerateLoginResponse) {
        localRepository.saveLoginInfo(loginDataResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                Timber.i(it)
            }).autoDispose()
    }

    fun getLoginInfo() {
        localRepository.getLoginInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                localStateSubject.onNext(LocalViewState.SuccessResponse(it))
            }, {
                Timber.i(it)
            }).autoDispose()
    }
}


sealed class LocalViewState {
    data class SuccessResponse(val response: GenerateLoginResponse) : LocalViewState()
}


