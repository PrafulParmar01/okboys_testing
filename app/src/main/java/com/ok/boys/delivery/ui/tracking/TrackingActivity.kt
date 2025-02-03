package com.ok.boys.delivery.ui.tracking

import AtPickupModel
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ok.boys.delivery.BuildConfig
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.orders.model.JobAddressItem
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastLong
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.locations.BaseLocationHelper
import com.ok.boys.delivery.databinding.ActivityTrackingBinding
import com.ok.boys.delivery.ui.orders.viewmodel.OrderProcessViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import com.ok.boys.delivery.util.ApiConstant
import com.psp.google.direction.GoogleDirection
import com.psp.google.direction.config.GoogleDirectionConfiguration
import com.psp.google.direction.constant.TransportMode
import com.psp.google.direction.model.Direction
import com.psp.google.direction.model.Route
import com.psp.google.direction.util.DirectionConverter
import com.psp.google.direction.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class TrackingActivity : BaseActivity(), OnMapReadyCallback, BaseLocationHelper.NewLocationListener {

    private lateinit var binding: ActivityTrackingBinding
    private lateinit var mMap: GoogleMap
    private var isZoom = false

    private var locationPermissionGranted = false
    private var baseLocationHelper: BaseLocationHelper? = null
    private var jobAddressList: List<JobAddressItem> = listOf()
    private var jobAddressItem: JobAddressItem? = null
    private var mOrderId: String = ""
    private var mOrderState: String = ""
    private var mPosition: Int = 0
    private lateinit var currentIcon: BitmapDescriptor
    private lateinit var destinationIcon: BitmapDescriptor
    private var wayPointList: MutableList<LatLng> = mutableListOf()

    @Inject
    lateinit var orderViewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private var origin: LatLng? = null
    private var destination: LatLng? = null

    private var directionJob: Job? = null
    private var alertDialog: AlertDialog? = null
    private val DEFAULT_ZOOM = 12
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private val GOOGLE_DIRECTIONS_API_KEY = "AIzaSyBdnTPrQvexr6f_ry5ILKuGPu-fVMREDus"

    companion object {
        fun getIntent(context: Context, orderId: String, position: Int, orderState: String): Intent {
            val intent = Intent(context, TrackingActivity::class.java)
            intent.putExtra("order_id", orderId)
            intent.putExtra("position", position)
            intent.putExtra("order_state", orderState)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
    }

    private fun initViews() {
        orderViewModel = getViewModelFromFactory(orderViewModelFactory)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        currentIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_marker)
        destinationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_marker)

        jobAddressList = OkBoysApplication.jobAddressList
        mOrderId = intent.getStringExtra("order_id") as String
        mPosition = intent.getIntExtra("position", 0)
        mOrderState = intent.getStringExtra("order_state") as String
        binding.trackingToolBar.txtTitle.text = "Tracking"

        if (mOrderState == ApiConstant.ORDER_UPLOAD_INVOICE || mOrderState == ApiConstant.ORDER_PAYMENT_REQUEST) {
            binding.btnPickupPoint.visibility = View.INVISIBLE
        } else {
            binding.btnPickupPoint.visibility = View.VISIBLE
        }

        binding.trackingToolBar.btnBack.setOnClickListener {
            finish()
        }

        addOnBackPressedDispatcher {
            finish()
        }

        if (jobAddressList.isNotEmpty()) {
            jobAddressItem = jobAddressList[mPosition] // DESTINATION
            jobAddressItem?.let {
                binding.txtUserName.text = it.area
                binding.txtDeliveryAddess.text = it.address
                destination = LatLng(it.lat!!, it.lng!!)
                Log.e("itemDestination: ", "===> " + it.address)
            }

            if (jobAddressList.size > 1) {
                for (i in 1 until jobAddressList.size) {
                    val item = jobAddressList[i]
                    Log.e("itemWayPoints: ", "===> " + item.address)
                    wayPointList.add(LatLng(item.lat!!, item.lng!!))
                }
            }
        }

        binding.btnPickupPoint.setOnClickListener {
            val orderPaymentRequest = OrderPaymentRequest(
                event = ApiConstant.ORDER_AT_PICKUP_POINT,
                orderJobId = jobAddressItem?.orderJobId
            )
            orderViewModel.getOrderProcessAPI(mOrderId, orderPaymentRequest)
        }

        binding.btnNavigation.setOnClickListener {
            if (origin != null && destination != null) {
                val mOrigin = latLngToString(origin!!)
                val mDestination = latLngToString(destination!!)
                val waypointsToString = waypointsToString(wayPointList)
                Log.e("wayPointStrings: ", "===> " + waypointsToString)

                val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$mOrigin&destination=$mDestination&waypoints=$waypointsToString&travelmode=driving")
                val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                intent.setPackage("com.google.android.apps.maps")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        val unrestrictedIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        startActivity(unrestrictedIntent)
                    } catch (innerEx: ActivityNotFoundException) {
                        toastShort("Please install a Google Maps application")
                    }
                }
            } else {
                toastShort("Please wait for current location")
            }
        }

        viewObservers()
    }

    private fun viewObservers() {
        orderViewModel.orderProcessViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderProcessViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        Log.e("TrackingResponse: ", "===> " + it.successMessage.response.toString())
                        OkBoysApplication.isOrderRefreshed = true
                        val atPickupModel = AtPickupModel(true, mOrderId, jobAddressItem?.name.toString())
                        EventBus.getDefault().postSticky(atPickupModel)
                        finish()
                    }
                }

                is OrderProcessViewState.ErrorMessage -> {
                    Log.e("TrackingResponse: ", "===> " + it.errorMessage.toString())
                    toastLong(it.errorMessage.errorTitle)
                }

                is OrderProcessViewState.LoadingState -> {
                    if (it.isLoading) {
                        jsDialogUtils.showProgressDialog()
                    } else {
                        jsDialogUtils.dismissDialog()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            updateLocationUI()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    updateLocationUI()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun updateLocationUI() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showLocationEnabledDialog()
                }

                withContext(Dispatchers.IO) {
                    baseLocationHelper = BaseLocationHelper(applicationContext)
                    baseLocationHelper?.initLocation()
                    baseLocationHelper?.connectLocation()
                }

                baseLocationHelper?.setOnNewLocationListener(this@TrackingActivity)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun showLocationEnabledDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.enable_gps))
        builder.setMessage(resources.getString(R.string.enable_gps_msg))
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.label_settings) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        builder.setNegativeButton(R.string.no_close_app) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.show()
    }


    override fun onNewLocation(locationResult: Location?, available: Boolean) {
        getDeviceLocation(locationResult)
    }

    private fun getDeviceLocation(location: Location?) {
        lifecycleScope.launch {
            try {
                if (locationPermissionGranted) {
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude

                        if (!isZoom) {
                            withContext(Dispatchers.Main) {
                                mMap.clear()
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
                                isZoom = true
                                origin = latLng
                                destination = LatLng(jobAddressItem?.lat!!, jobAddressItem?.lng!!)
                            }
                            requestDirection()
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun requestDirection() {
        directionJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                GoogleDirectionConfiguration.getInstance().isLogEnabled = BuildConfig.DEBUG
                GoogleDirection.withServerKey(GOOGLE_DIRECTIONS_API_KEY)
                    .from(origin)
                    .and(wayPointList)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(
                        onDirectionSuccess = { direction ->
                            onDirectionSuccess(direction)
                        },
                        onDirectionFailure = { t ->
                            onDirectionFailure(t)
                        }
                    )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showSnackBar("Failed to fetch directions: ${e.message}")
                }
            }
        }
    }

    private fun onDirectionSuccess(direction: Direction) {
        lifecycleScope.launch(Dispatchers.Main) {
            val color = ContextCompat.getColor(mContext, R.color.color_yellow)
            if (direction.isOK) {
                val route = direction.routeList[0]
                val legCount = route.legList.size
                for (index in 0 until legCount) {
                    val leg = route.legList[index]
                    if (index == 0) {
                        mMap.addMarker(MarkerOptions().position(leg.startLocation.coordination).icon(currentIcon).title(leg.startAddress))
                        mMap.addMarker(MarkerOptions().position(leg.endLocation.coordination).icon(destinationIcon).title(leg.startAddress))
                    }

                    if (index == 1) {
                        mMap.addMarker(MarkerOptions().position(leg.endLocation.coordination).icon(destinationIcon).title(leg.startAddress))
                    }

                    wayPointList.forEach {
                        mMap.addMarker(MarkerOptions().position(it).icon(destinationIcon).title(leg.startAddress))
                    }

                    val stepList = leg.directionPoint
                    val polylineOptionList = DirectionConverter.createPolyline(this@TrackingActivity, stepList, 5, color)
                    mMap.addPolyline(polylineOptionList)
                }
                setCameraWithCoordinationBounds(route)
            } else {
                showSnackBar(direction.status)
            }
        }
    }

    private fun onDirectionFailure(t: Throwable) {
        lifecycleScope.launch(Dispatchers.Main) {
            showSnackBar(t.message)
        }
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun showSnackBar(message: String?) {
        message?.let {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun waypointsToString(waypoints: List<LatLng>): String {
        return waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
    }

    private fun latLngToString(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (baseLocationHelper!=null) {
            baseLocationHelper?.disconnectLocation()
            baseLocationHelper = null
        }
        directionJob?.cancel()
        alertDialog?.dismiss()
        mMap.clear()
    }
}