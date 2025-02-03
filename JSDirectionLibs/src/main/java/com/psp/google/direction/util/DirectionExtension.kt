package com.psp.google.direction.util

import com.psp.google.direction.DirectionCallback
import com.psp.google.direction.model.Direction
import com.psp.google.direction.request.DirectionRequest
import com.psp.google.direction.request.DirectionTask


fun DirectionRequest.execute(
    onDirectionSuccess: ((Direction) -> Unit)? = null,
    onDirectionFailure: ((Throwable) -> Unit)? = null
): DirectionTask = execute(object : DirectionCallback {
    override fun onDirectionSuccess(direction: Direction) {
        onDirectionSuccess?.invoke(direction)
    }

    override fun onDirectionFailure(t: Throwable) {
        onDirectionFailure?.invoke(t)
    }
})
