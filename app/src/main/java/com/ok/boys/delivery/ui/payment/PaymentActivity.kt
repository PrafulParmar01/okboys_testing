package com.ok.boys.delivery.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentFeesRequest
import com.ok.boys.delivery.base.api.orders.model.PaymentProcessFeesRequest
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastLong
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.databinding.ActivityPaymentBinding
import com.ok.boys.delivery.ui.orders.viewmodel.GenerateOrderProcessViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderProcessViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import com.ok.boys.delivery.util.ApiConstant
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import javax.inject.Inject


class PaymentActivity : BaseActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityPaymentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel

    private var mOrderId = ""

    companion object {
        fun getIntent(orderId: String, context: Context): Intent {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("order_id", orderId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
    }

    private fun initViews() {
        Checkout.preload(this)
        orderViewModel = getViewModelFromFactory(viewModelFactory)
        mOrderId = intent.getStringExtra("order_id").toString()
        binding.btnPayNow.setOnClickListener {
            createOrder()
            //startPayment(orderId)
        }

        binding.btnRetry.setOnClickListener {
            createOrder()
            //startPayment(orderId)
        }

        onPaymentStarting()
        viewModelObservers()
    }

    private fun viewModelObservers() {
        orderViewModel.orderProcessViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderProcessViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        OkBoysApplication.isOrderRefreshed = false
                        val intent = Intent("order_completed")
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        finish()
                    } else {
                        toastShort(onErrorNoData().errorDescription)
                    }
                }

                is OrderProcessViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }

                is OrderProcessViewState.LoadingState -> {
                    if (it.isLoading) {
                        binding.layoutProgressSuccess.visibility = View.VISIBLE
                    } else {
                        binding.layoutProgressSuccess.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()


        orderViewModel.generateOrderProcessViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is GenerateOrderProcessViewState.SuccessResponse -> {

                }

                is GenerateOrderProcessViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }

                is GenerateOrderProcessViewState.LoadingState -> {
                    if (it.isLoading) {
                        binding.layoutProgressSuccess.visibility = View.VISIBLE
                    } else {
                        binding.layoutProgressSuccess.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()
    }

    private fun createOrder(){
        val generateOrderRequest = GenerateOrderRequest(serviceFeeAmount = "5000")
        orderViewModel.generateOrderRequest(mOrderId,generateOrderRequest)
    }


    private fun startPayment(orderId: String?) {
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "OkBoys Delivery")
            options.put("description", "Pay Service fees")
            options.put("currency", "INR")
            options.put("amount", "500")
            options.put("send_sms_hash", true)
            options.put("timeout", 5000)
            options.put("theme.color", "#FFAD01")
            options.put("allow_rotation", false)
            options.put("order_id", orderId);

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 5)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email", "support@okboys.in")
            prefill.put("contact", "9849402299")
            options.put("prefill", prefill)

            co.open(this, options)
        } catch (e: Exception) {
            toastLong("Error in payment")
            onPaymentFailedUI()
            e.printStackTrace()
        }
    }


    private fun onPaymentStarting() {
        binding.layoutStarting.visibility = View.VISIBLE
        binding.layoutSuccess.visibility = View.GONE
        binding.layoutFailed.visibility = View.GONE
    }


    private fun onPaymentSuccessUI(transactionId: String) {
        binding.layoutStarting.visibility = View.GONE
        binding.layoutSuccess.visibility = View.VISIBLE
        binding.layoutFailed.visibility = View.GONE
        onCallPaymentServiceFees(transactionId)
    }

    private fun onPaymentFailedUI() {
        binding.layoutStarting.visibility = View.GONE
        binding.layoutSuccess.visibility = View.GONE
        binding.layoutFailed.visibility = View.VISIBLE
    }

    private fun onCallPaymentServiceFees(transactionId: String) {
        val requestOrder = OrderPaymentFeesRequest()
        requestOrder.event = ApiConstant.ORDER_PAY_SERVICE_FEE
        val paymentProcessFeesRequest = PaymentProcessFeesRequest()
        paymentProcessFeesRequest.transactionId = transactionId
        paymentProcessFeesRequest.transactionMode = "UPI"
        paymentProcessFeesRequest.transactionStatus = "COMPLETED"
        paymentProcessFeesRequest.upiHandle = ApiConstant.UPI_HANDLE
        paymentProcessFeesRequest.amount = 1
        requestOrder.orderProcessFee = paymentProcessFeesRequest
        orderViewModel.getOrderPaymentFeesRequest(orderId = mOrderId, requestOrder)
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        onPaymentSuccessUI(p1?.paymentId.toString())
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        onPaymentFailedUI()
    }
}