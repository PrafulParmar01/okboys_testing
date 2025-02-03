package com.ok.boys.delivery.ui.orders.view.payment.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.orders.model.JobPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.chunking.ImageManager
import com.ok.boys.delivery.chunking.viewmodel.ImageViewModel
import com.ok.boys.delivery.databinding.ActivityRequestPaymentBinding
import com.ok.boys.delivery.receivers.NetworkConnectivityReceiver
import com.ok.boys.delivery.ui.orders.viewmodel.OrderProcessViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import com.ok.boys.delivery.util.ApiConstant
import javax.inject.Inject


class PaymentRequestActivity : BaseActivity() {

    lateinit var binding: ActivityRequestPaymentBinding
    private var mOrderId: String = ""

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel


    @Inject
    lateinit var imageModelFactory: ViewModelFactory<ImageViewModel>
    private lateinit var imageViewModel: ImageViewModel

    private lateinit var imageManager: ImageManager

    companion object {
        fun getIntent(context: Context, orderId: String): Intent {
            val intent = Intent(context, PaymentRequestActivity::class.java)
            intent.putExtra("order_id", orderId)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
        viewModelObservers()
    }


    private fun viewModelObservers() {
        orderViewModel.orderProcessViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderProcessViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        finish()
                        OkBoysApplication.isOrderRefreshed = true
                    }
                }

                is OrderProcessViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }

                is OrderProcessViewState.LoadingState -> {
                    if (it.isLoading) {
                        jsDialogUtils.showProgressDialog()
                    } else {
                        jsDialogUtils.dismissDialog()
                    }
                }
            }
        }.autoDispose()
    }

    private fun initViews() {
        ApiConstant.IS_TOKEN_PASSED = true

        orderViewModel = getViewModelFromFactory(viewModelFactory)
        mOrderId = intent.getStringExtra("order_id") as String

        imageViewModel = getViewModelFromFactory(imageModelFactory)

        imageManager = ImageManager(applicationContext)
        ImageManager.INCREMENTAL_COUNTER = 0
        ImageManager.END_SIZE_COUNTER = 0

        binding.toolBarHome.txtTitle.text = "Request Payment"

        binding.btnRequestPayment.setOnClickListener {
            if (binding.editAmount.text.isNullOrEmpty()) {
                toastShort("Please add valid amount")
            } else {
                if (NetworkConnectivityReceiver().isInternetAvailable(applicationContext)) {
                    onCallRequestPayment()
                } else {
                    toastShort("No Internet Connection")
                }
            }
        }

        binding.toolBarHome.btnBack.setOnClickListener {
            finish()
        }
    }


    private fun onCallRequestPayment() {
        val jobAddressItem = OkBoysApplication.jobAddressItem
        if (jobAddressItem != null) {

            val jobPaymentRequest = JobPaymentRequest(
                amount = binding.editAmount.text.toString().trim().toDouble(),
                transactionMode = "UPI",
            )

            val orderPaymentRequest = OrderPaymentRequest(
                event = "REQUEST_PAYMENT",
                orderJobId = jobAddressItem.orderJobId.toString(),
                jobPayment = jobPaymentRequest,
            )

            orderViewModel.getOrderProcessAPI(
                orderId = mOrderId,
                orderPaymentRequest
            )
        }
    }
}