package com.ok.boys.delivery.util

import android.os.Handler
import android.os.Looper
import timber.log.Timber

class TrackerTimer {

    val timeHandler: Handler = Handler(Looper.getMainLooper())
    var isRunning = false
    var stopHandler = false

    private var mSeconds = 1000L
    private var mTimerTickListener: TimerTickListener? = null

    val timeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!stopHandler) {
                try {
                    isRunning = true
                    if (mTimerTickListener != null) {
                        mTimerTickListener?.onTickListener(mSeconds)
                    }
                    mSeconds += 1000L
                    timeHandler.postDelayed(this, UPDATE_INTERVAL)
                } catch (e: Exception) {
                    e.printStackTrace()
                    isRunning = false
                }
            }
        }
    }

    fun removeTimerCallbacks() {
        timeHandler.removeCallbacksAndMessages(null)
        isRunning = false
        stopHandler = true
        mSeconds = 1000L
        Timber.e("removeTimerCallbacks: ===> Done")
    }

    interface TimerTickListener {
        fun onTickListener(milliSeconds: Long)
    }

    fun setOnTimeListener(timerTickListener: TimerTickListener) {
        mTimerTickListener = timerTickListener
    }

    companion object {
         const val UPDATE_INTERVAL = 20000L
    }
}