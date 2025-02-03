package com.ok.boys.delivery.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.throttleClicks
import com.ok.boys.delivery.databinding.ActivityProfileBinding
import com.ok.boys.delivery.dialogs.LogoutDialog
import com.ok.boys.delivery.ui.login.WelcomeActivity
import com.ok.boys.delivery.ui.login.viewmodel.LocalViewModel
import com.ok.boys.delivery.ui.login.viewmodel.LocalViewState
import com.ok.boys.delivery.util.PrefUtil
import javax.inject.Inject


class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var localModelFactory: ViewModelFactory<LocalViewModel>
    private lateinit var localViewModel: LocalViewModel

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
        viewModelObservers()
    }

    private fun viewModelObservers() {
        localViewModel.localViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is LocalViewState.SuccessResponse -> {
                    val response = it.response.response
                    val userDetails = response?.userDetails
                    binding.txtUserName.text = userDetails?.userName.toString()
                    binding.txtUserEmail.text = userDetails?.email.toString()
                    binding.txtUserMobile.text = userDetails?.mobileNumber.toString()
                }

                else -> {}
            }
        }.autoDispose()

    }

    private fun initViews() {
        localViewModel = getViewModelFromFactory(localModelFactory)
        addOnBackPressedDispatcher {
            finish()
        }

        binding.btnLogout.throttleClicks().subscribe {
            val logout = LogoutDialog()
            logout.openDialog(this)
            logout.setOnDialogListener(object :LogoutDialog.OnClickListener{
                override fun onClick() {
                    onLogoutClicked()
                }
            })
        }.autoDispose(compositeDisposable)




        binding.profileToolBar.btnBack.throttleClicks().subscribe {
           onBackPressed()
        }.autoDispose(compositeDisposable)

        binding.profileToolBar.txtTitle.text = "Profile"
        localViewModel.getLoginInfo()
    }

    private fun onLogoutClicked() {
        PrefUtil.putBooleanPref(PrefUtil.PRF_IS_LOGIN, false, mContext)
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}