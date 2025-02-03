package com.ok.boys.delivery.base.view

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val compositeDisposable = CompositeDisposable()


    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}