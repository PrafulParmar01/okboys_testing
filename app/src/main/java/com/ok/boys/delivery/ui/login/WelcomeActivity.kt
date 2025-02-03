package com.ok.boys.delivery.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.login.model.GenerateOTPRequest
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.startActivityWithFadeInAnimation
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.databinding.ActivityWelcomeBinding
import com.ok.boys.delivery.ui.login.viewmodel.LoginViewModel
import com.ok.boys.delivery.ui.login.viewmodel.OTPViewState
import com.ok.boys.delivery.util.ApiConstant
import javax.inject.Inject


class WelcomeActivity : BaseActivity() {

    lateinit var binding: ActivityWelcomeBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<LoginViewModel>
    private lateinit var loginViewModel: LoginViewModel

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
        initClicks()
        listenToViewModel()
    }

    private fun initViews() {
        loginViewModel = getViewModelFromFactory(viewModelFactory)
        addOnBackPressedDispatcher {
            finish()
        }

        binding.layoutPhone.layoutTerms.btnTermsConditions.setOnClickListener {
            val url = "https://dev.app.okboys.in/deliveryboys/terms-of-service"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
    }

    private fun initClicks() {
        binding.layoutPhone.btnGetStarted.setOnClickListener {
            if (!isCheckValidation()) {
                val countryCode = "+91"
                val reqLogin = GenerateOTPRequest()
                reqLogin.mobileNumber = countryCode + binding.layoutPhone.editLogin.text.toString().trim()
                ApiConstant.IS_TOKEN_PASSED = false
                loginViewModel.getGenerateOTP(reqLogin)
            }
        }
    }

    private fun isCheckValidation(): Boolean {
        var isCheck = false
        val valuePhone = binding.layoutPhone.editLogin.text.toString().trim()
        if (valuePhone.isEmpty()) {
            isCheck = true
            toastShort(getString(R.string.label_error_enter_mob_num))
        }
        return isCheck
    }


    private fun listenToViewModel() {
        loginViewModel.otpViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OTPViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        val valuePhone = binding.layoutPhone.editLogin.text.toString().trim()
                        toastShort(getString(R.string.msg_otp_sent_successfully))
                        val otpValue = it.successMessage.response
                        startActivityWithFadeInAnimation(OTPActivity.getIntent(mContext,valuePhone,otpValue))
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