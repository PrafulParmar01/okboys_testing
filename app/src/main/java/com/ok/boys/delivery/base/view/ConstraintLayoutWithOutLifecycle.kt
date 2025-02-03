package com.ok.boys.delivery.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * This uses the parent context to add observables to the view for parent lifecycle events
 */
abstract class ConstraintLayoutWithOutLifecycle(
    context: Context, attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), LifecycleObserver {
    private val compositeDisposable = CompositeDisposable()

    open fun onDestroy() {
        compositeDisposable.clear()
    }

    protected fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    protected fun destroy() {
        compositeDisposable.clear()
    }
}