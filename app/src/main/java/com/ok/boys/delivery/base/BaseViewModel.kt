package com.ok.boys.delivery.base

import androidx.lifecycle.ViewModel
import com.ok.boys.delivery.base.network.ErrorViewState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.IOException
import java.net.UnknownHostException

open class BaseViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    fun onHandleError(it: Throwable?): ErrorViewState {
        return if (it is UnknownHostException || it is IOException) {
            ErrorViewState("No Internet Connection","Please check your network connection")
        } else {
            ErrorViewState("Connection Timeout","Something went wrong. Please try again")
        }
    }
}