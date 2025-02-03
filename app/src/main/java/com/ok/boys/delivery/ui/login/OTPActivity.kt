package com.ok.boys.delivery.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.login.model.GenerateOTPRequest
import com.ok.boys.delivery.base.api.login.model.VerifyOTPRequest
import com.ok.boys.delivery.base.extentions.*
import com.ok.boys.delivery.databinding.ActivityOtpBinding
import com.ok.boys.delivery.ui.dashboard.HomeActivity
import com.ok.boys.delivery.ui.login.viewmodel.LocalViewModel
import com.ok.boys.delivery.ui.login.viewmodel.LoginViewModel
import com.ok.boys.delivery.ui.login.viewmodel.LoginViewState
import com.ok.boys.delivery.ui.login.viewmodel.OTPViewState
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.HandlerTimer
import com.ok.boys.delivery.util.PrefUtil
import com.ok.boys.delivery.util.UtilsMethod
import javax.inject.Inject


class OTPActivity : BaseActivity() {

    private lateinit var binding: ActivityOtpBinding
    var mobileNumber: String? = null
    lateinit var otp: String

    private lateinit var handlerTimer: HandlerTimer
    private lateinit var timeHandler: Handler
    private lateinit var timeRunnable: Runnable


    @Inject
    lateinit var viewModelFactory: ViewModelFactory<LoginViewModel>
    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var localModelFactory: ViewModelFactory<LocalViewModel>
    private lateinit var localViewModel: LocalViewModel

    private var fcmId = ""
    private var valueOTP = ""

    companion object {
        fun getIntent(context: Context, number: String,valueOTP: String): Intent {
            val intent = Intent(context, OTPActivity::class.java)
            intent.putExtra("mobile_number", number)
            intent.putExtra("value_otp", valueOTP)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
        initClicks()
        listenToViewModel()
    }

    private fun initClicks() {
        binding.layoutOTP.btnNext.setOnClickListener {
            if (!isCheckValidation()) {
                val countryCode = "+91"
                otp = binding.layoutOTP.otpView.otp.toString().trim()
                val verifyOTPRequest = VerifyOTPRequest()
                verifyOTPRequest.mobileNumber = countryCode.plus(mobileNumber)
                verifyOTPRequest.otp = otp
                verifyOTPRequest.fcmId = fcmId
                ApiConstant.IS_TOKEN_PASSED = false
                loginViewModel.getVerifyOTP(verifyOTPRequest)
            }
        }

        binding.layoutOTP.btnResendCode.setOnClickListener {
            val countryCode = "+91"
            val reqLogin = GenerateOTPRequest()
            reqLogin.mobileNumber = countryCode + mobileNumber
            loginViewModel.getGenerateOTP(reqLogin)
        }
    }

    @SuppressLint("LogNotTimber")
    private fun initViews() {
        loginViewModel = getViewModelFromFactory(viewModelFactory)
        localViewModel = getViewModelFromFactory(localModelFactory)

        mobileNumber = intent.getStringExtra("mobile_number") as String
        valueOTP = intent.getStringExtra("value_otp") as String
        binding.layoutOTP.otpView.otp = valueOTP
        addOnBackPressedDispatcher {
            finish()
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("token", "FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                fcmId = task.result
                Log.e("token", "FCM registration token failed ===> $fcmId")
            })

        handlerTimer = HandlerTimer()
        timeHandler = handlerTimer.timeHandler
        timeRunnable = handlerTimer.timeRunnable
        onTimerStart()

        handlerTimer.setOnTimeListener(object : HandlerTimer.TimerTickListener {
            override fun onTickListener(milliSeconds: Long) {
                if (milliSeconds == 0L) {
                    onTimerStop()
                } else {
                    val second = milliSeconds / 1000
                    val convertSeconds = UtilsMethod.convertSeconds(second)
                    val text = String.format(
                        resources.getString(R.string.label_otp_time_out),
                        convertSeconds
                    )
                    binding.layoutOTP.btnResendCode.visibility = View.GONE
                    binding.layoutOTP.txtResendCount.visibility = View.VISIBLE
                    binding.layoutOTP.txtResendCount.text = text
                    Log.e("startTimer: ", "==+ $milliSeconds")
                }
            }
        })

        binding.layoutOTP.layoutTerms.btnTermsConditions.setOnClickListener {
            val url = "https://dev.app.okboys.in/deliveryboys/terms-of-service"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
    }


    private fun isCheckValidation(): Boolean {
        var isCheck = false
        val valueOTP = binding.layoutOTP.otpView.otp.toString()
        if (valueOTP.isEmpty()) {
            isCheck = true
            toastShort(getString(R.string.label_enter_otp))
        }
        return isCheck
    }

    private fun onTimerStop() {
        binding.layoutOTP.btnResendCode.visibility = View.VISIBLE
        binding.layoutOTP.txtResendCount.visibility = View.GONE
        handlerTimer.removeTimerCallbacks()
    }

    private fun onTimerStart() {
        if (!handlerTimer.isRunning) {
            handlerTimer.stopHandler = false
            timeHandler.postDelayed(timeRunnable, 0L)
        }
    }


    private fun listenToViewModel() {
        loginViewModel.loginViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is LoginViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        toastShort(getString(R.string.msg_login_successfully))
                        val accessToken = it.successMessage.response?.loginResponse?.accessToken
                        val userId = it.successMessage.response?.userDetails?.id

                        PrefUtil.putBooleanPref(PrefUtil.PRF_IS_LOGIN, true, mContext)
                        PrefUtil.putStringPref(PrefUtil.PRF_ACCESS_TOKEN, accessToken, mContext)
                        PrefUtil.putStringPref(PrefUtil.PREF_USER_ID, userId, mContext)
                        localViewModel.saveLoginInfo(it.successMessage)
                        onTimerStop()

                        val intent = HomeActivity.getIntent(false,this)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivityWithFadeInAnimation(intent)
                    } else {
                        toastShort(onErrorNoData().errorDescription)
                    }
                }
                is LoginViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }
                is LoginViewState.LoadingState -> {
                    if (it.isLoading) {
                        jsDialogUtils.showProgressDialog()
                    } else {
                        jsDialogUtils.dismissDialog()
                    }
                }
            }
        }.autoDispose()


        loginViewModel.otpViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OTPViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        toastLong(getString(R.string.msg_otp_sent_successfully))
                        valueOTP = it.successMessage.response
                        binding.layoutOTP.otpView.setOTP(valueOTP)
                        onTimerStart()
                    } else {
                        toastShort(onErrorNoData().errorDescription)
                    }
                }
                is OTPViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }
                is OTPViewState.LoadingState -> {
                    if (it.isLoading) {
                        jsDialogUtils.showProgressDialog()
                    } else {
                        jsDialogUtils.dismissDialog()
                    }
                }
            }
        }.autoDispose()
    }
}