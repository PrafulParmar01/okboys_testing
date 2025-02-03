package com.ok.boys.delivery.ui.dashboard.view

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseFragment
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.home.model.EarningsItem
import com.ok.boys.delivery.base.extentions.getViewModel
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.databinding.FragmentHomeBinding
import com.ok.boys.delivery.receivers.NetworkConnectivityReceiver
import com.ok.boys.delivery.services.LocationRequestEvent
import com.ok.boys.delivery.services.LocationUtils
import com.ok.boys.delivery.services.LocationsService
import com.ok.boys.delivery.ui.dashboard.viewmodel.HomeViewModel
import com.ok.boys.delivery.ui.dashboard.viewmodel.HomeViewState
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.ApiConstant.IS_HOME
import com.ok.boys.delivery.util.PrefUtil
import com.ok.boys.delivery.util.UtilsMethod
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.permissionx.guolindev.PermissionX
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    private lateinit var locationUtils: LocationUtils
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter
    private var listOfData: List<EarningsItem> = listOf()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>
    private lateinit var homeViewModel: HomeViewModel

    private var isChecked = false
    private var isFirstTime = false
    private var isPermissionGranted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OkBoysApplication.component.inject(this)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        viewModelObservers()
        binding.swipeRefreshLayout.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                isFirstTime = true
                onSyncGetUserData()
            }
        }
        onSyncPermission()
    }

    private fun onSyncPermission() {
        val permissionX = PermissionX.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionX.permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else {
            permissionX.permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    isPermissionGranted = true
                    locationUtils.startLocationRequest()
                } else {
                    requireActivity().toastShort("Please allow permissions")
                }
            }
    }


    private fun onSyncLocationService() {
        if (!UtilsMethod.isServiceRunning(baseActivity, LocationsService::class.java)) {
            baseActivity.startService(LocationsService.getIntent(requireContext()))
        }
    }


    private fun viewModelObservers() {
        homeViewModel.homeViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is HomeViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        val orderResponse = it.successMessage.response?.orderResponse
                        val isBroadcasted = orderResponse == null
                        PrefUtil.putBooleanPref(PrefUtil.PREF_IS_BROADCASTED, isBroadcasted, requireContext())

                        if (it.successMessage.response?.earnings!!.isNotEmpty()) {
                            binding.rvHome.visibility = View.VISIBLE
                            binding.layoutData.parentView.visibility = View.GONE
                            listOfData = it.successMessage.response.earnings
                            homeAdapter.updateListInfo(listOfData)
                        } else {
                            onHandleError(false, onErrorNoData(IS_HOME))
                        }
                        isChecked = it.successMessage.response.onDuty
                        if (isChecked) {
                            binding.switchDuty.setImageResource(R.drawable.ic_switch_on)
                        } else {
                            binding.switchDuty.setImageResource(R.drawable.ic_switch_off)
                        }
                    } else {
                        onHandleError(false, onErrorNoData(IS_HOME))
                    }
                }

                is HomeViewState.UserDutyStatusResponse -> {
                    if (it.statusMessage.statusCode == 200) {
                        isChecked = it.statusMessage.response?.inDuty!!
                        if (isChecked) {
                            requireActivity().toastShort("Duty ON successfully")
                            binding.switchDuty.setImageResource(R.drawable.ic_switch_on)
                        } else {
                            requireActivity().toastShort("Duty OFF successfully")
                            binding.switchDuty.setImageResource(R.drawable.ic_switch_off)
                        }
                    }
                }

                is HomeViewState.ErrorMessage -> {
                    onHandleError(true, it.errorMessage)
                }

                is HomeViewState.LoadingState -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (!isFirstTime) {
                        if (it.isLoading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()
    }

    private fun onHandleError(isError: Boolean, it: ErrorViewState) {
        if (isError) {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.rvHome.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        } else {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.rvHome.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtWelcome.text = "WELCOME"
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        }
    }


    private fun initView() {
        locationUtils = LocationUtils(requireActivity())
        homeViewModel = getViewModel(viewModelFactory)
        homeAdapter = HomeAdapter(requireContext())
        binding.rvHome.adapter = homeAdapter
        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())

        binding.switchDuty.setOnClickListener {
            if (isChecked) {
                if (NetworkConnectivityReceiver().isInternetAvailable(requireContext())) {
                    homeViewModel.setUserDutyStatus(false)
                }
            } else {
                if (NetworkConnectivityReceiver().isInternetAvailable(requireContext())) {
                    homeViewModel.setUserDutyStatus(true)
                }
            }
        }
        onSyncGetUserData()
    }


    private fun onSyncGetUserData() {
        ApiConstant.IS_TOKEN_PASSED = true
        val userId = PrefUtil.getStringPref(PrefUtil.PREF_USER_ID, requireContext())
        homeViewModel.getUsersById(userId)
    }


    override fun onResume() {
        super.onResume()
        val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (isPermissionGranted) {
                onSyncLocationService()
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLocationRequestEvent(locationRequest: LocationRequestEvent) {
        if (!locationRequest.isSuccess) {
            val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationUtils.onEnabledLocationDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}