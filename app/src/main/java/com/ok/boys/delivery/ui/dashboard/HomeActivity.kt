package com.ok.boys.delivery.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.startActivityWithFadeInAnimation
import com.ok.boys.delivery.base.extentions.throttleClicks
import com.ok.boys.delivery.databinding.ActivityHomeBinding
import com.ok.boys.delivery.ui.dashboard.view.HomeFragment
import com.ok.boys.delivery.ui.history.view.HistoryFragment
import com.ok.boys.delivery.ui.orders.view.OrdersFragment
import com.ok.boys.delivery.ui.payment.OrderCompletedEvent
import com.ok.boys.delivery.ui.profile.ProfileActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeActivity : BaseActivity() {


    companion object {
        fun getIntent(isFromNotification: Boolean, context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("is_from_notification", isFromNotification)
            return intent
        }
    }

    var chatCount = 0
    lateinit var binding: ActivityHomeBinding
    private var isFromNotification = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
    }

    private fun initViews() {
        addOnBackPressedDispatcher {
            finish()
        }
        setupClickListener()
        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("custom-action-local-broadcast"))
        isFromNotification = intent.getBooleanExtra("is_from_notification", false)
        if (isFromNotification) {
            onLoadOrdersFragment()
        } else {
            defaultFragment()
        }
    }



    private fun defaultFragment() {
        binding.toolBarHome.txtTitle.text = "Home"
        binding.rlHome.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_selected)
        binding.rlOrder.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
        binding.rlHistory.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
        replaceFragment(HomeFragment(), binding.fragContainer.id)
    }


    private fun setupClickListener() {
        binding.toolBarHome.btnProfile.throttleClicks().subscribe {
            startActivityWithFadeInAnimation(ProfileActivity.getIntent(this))
        }.autoDispose(compositeDisposable)


        binding.rlHome.throttleClicks().subscribe {
            defaultFragment()
        }.autoDispose(compositeDisposable)

        binding.rlOrder.throttleClicks().subscribe {
            onLoadOrdersFragment()
        }.autoDispose(compositeDisposable)

        binding.rlHistory.throttleClicks().subscribe {
            onLoadHistoryFragment()
        }.autoDispose(compositeDisposable)
    }

    private fun onLoadHistoryFragment() {
        binding.toolBarHome.txtTitle.text = "History"
        binding.rlHome.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
        binding.rlOrder.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
        binding.rlHistory.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_selected)
        replaceFragment(HistoryFragment(), binding.fragContainer.id)
    }

    private fun onLoadOrdersFragment() {
        try {
            binding.toolBarHome.txtTitle.text = "Orders"
            binding.rlHome.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
            binding.rlOrder.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_selected)
            binding.rlHistory.background = ContextCompat.getDrawable(this, R.drawable.rounded_tab_unselected)
            replaceFragment(OrdersFragment(), binding.fragContainer.id)
        }catch (e:Exception){
            e.printStackTrace()
            Log.e("onLoadOrdersFragment", "Exception: " + e.message)
        }

    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onOrderCompletedEvent(event: OrderCompletedEvent) {
        if (event.isSuccess){
           defaultFragment()
        }
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}