package com.ok.boys.delivery.ui.splash

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.extentions.startActivityWithFadeInAnimation
import com.ok.boys.delivery.databinding.ActivityStartBinding
import com.ok.boys.delivery.ui.chat.view.ChatActivity
import com.ok.boys.delivery.ui.dashboard.HomeActivity
import com.ok.boys.delivery.ui.login.WelcomeActivity
import com.ok.boys.delivery.util.AppSignatureHelper
import com.ok.boys.delivery.util.PrefUtil
import com.ok.boys.delivery.util.UtilsMethod
import org.json.JSONException


class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding
    private var splashTime = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            val version = UtilsMethod.getCurrentVersionName(this)
            val text = String.format(resources.getString(R.string.label_app_Version), version)
            binding.txtVersionNumber.text = text
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val bundle = intent.extras
        if (bundle != null) {
            try {
                var type = ""
                var orderId = ""
                var jobState = ""
                if (bundle.containsKey("orderId")) {
                    orderId = bundle.getString("orderId") as String
                    Log.e("orderId: ", "===> " + orderId)
                }

                if (bundle.containsKey("type")) {
                    type = bundle.getString("type") as String
                    Log.e("type: ", "===> " + type)
                }

                if (bundle.containsKey("jobState")) {
                    jobState = bundle.getString("jobState") as String
                    Log.e("jobState: ", "===> " + jobState)
                }

                if ((type == "COMMENT") or (type == "UPLOADS")) {
                    finish()
                    startActivityWithFadeInAnimation(ChatActivity.getIntent(orderId, true, 1,"",this))
                } else if ((type == "INVOICE_UPLOADS") or (type == "STATE")) {
                    //finish()
                    //startActivityWithFadeInAnimation(HomeActivity.getIntent(true, this))
                } else if (type == "New Order") {
                    finish()
                    startActivityWithFadeInAnimation(HomeActivity.getIntent(true, this))
                } else if (type == "EDIT-ORDER") {
                    finish()
                    startActivityWithFadeInAnimation(HomeActivity.getIntent(true, this))
                }
                else if ((jobState == "PAYMENT_STARTED") or (jobState == "PAYMENT_CONFIRMED")) {
                    finish()
                    startActivityWithFadeInAnimation(HomeActivity.getIntent(true, this))
                }
                else {
                    initViews()
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
        } else {
            initViews()
        }
    }

    private fun initViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            val isLogin = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_LOGIN, mContext)
            if (isLogin) {
                startActivityWithFadeInAnimation(HomeActivity.getIntent(false, this@StartActivity))
            } else {
                startActivityWithFadeInAnimation(WelcomeActivity.getIntent(this@StartActivity))
            }
            finish()
        }, splashTime)

        val appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper.appSignatures
    }
}