package com.ok.boys.delivery.ui.tracking


data class TrackingModel(
    var mOrderId: String = "",
    var position: Int = 0,
    val workflowState: String = ""
)
