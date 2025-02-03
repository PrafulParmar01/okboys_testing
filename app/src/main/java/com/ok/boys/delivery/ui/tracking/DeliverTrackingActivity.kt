package com.ok.boys.delivery.ui.tracking

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
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.UserAddressItem
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.locations.BaseLocationHelper
import com.ok.boys.delivery.databinding.ActivityDeliverTrackingBinding
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
import javax.inject.Inject


class DeliverTrackingActivity : BaseActivity(), OnMapReadyCallback,
    BaseLocationHelper.NewLocationListener {

    lateinit var binding: ActivityDeliverTrackingBinding
    private lateinit var mMap: GoogleMap
    private var isZoom = false

    private var locationPermissionGranted = false
    private var baseLocationHelper: BaseLocationHelper? = null
    private var userAddressItem: UserAddressItem? = null
    private var mOrderId: String = ""
    private lateinit var currentIcon: BitmapDescriptor
    private lateinit var destinationIcon: BitmapDescriptor

    private var origin: LatLng? = null
    private var destination: LatLng? = null

    @Inject
    lateinit var orderViewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private var directionJob: Job? = null
    private var alertDialog: AlertDialog? = null
    private val DEFAULT_ZOOM = 12
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private val serverKey = "AIzaSyBdnTPrQvexr6f_ry5ILKuGPu-fVMREDus"

    companion object {
        fun getIntent(context: Context, orderId: String): Intent {
            val intent = Intent(context, DeliverTrackingActivity::class.java)
            intent.putExtra("order_id", orderId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliverTrackingBinding.inflate(layoutInflater)
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

        userAddressItem = OkBoysApplication.userAddressItem
        mOrderId = intent.getStringExtra("order_id") as String
        binding.trackingToolBar.txtTitle.text = "Tracking"

        binding.trackingToolBar.btnBack.setOnClickListener {
            finish()
        }

        addOnBackPressedDispatcher {
            finish()
        }

        userAddressItem.let {
            binding.txtUserName.text = it?.area
            binding.txtDeliveryAddess.text = it?.address
        }

        binding.btnDeliveryPoint.setOnClickListener {
            val orderPaymentRequest = OrderPaymentRequest(
                event = ApiConstant.ORDER_AT_DELIVERY_POINT
            )
            orderViewModel.getOrderProcessAPI(mOrderId, orderPaymentRequest)
        }

        binding.btnNavigation.setOnClickListener {
            if (origin != null && destination != null) {
                val mOrigin = latLngToString(origin!!)
                val mDestination = latLngToString(destination!!)

                val gmmIntentUri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$mOrigin&destination=$mDestination&travelmode=driving")
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
                        finish()
                        OkBoysApplication.isOrderRefreshed = true
                    } else {
                        toastShort(onErrorNoData().errorDescription)
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
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()
    }


    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

    private fun latLngToString(waypoints: LatLng): String {
        val result = "${waypoints.latitude},${waypoints.longitude}"
        Log.e("latLngToString: ", "===> $result")
        return result
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

                baseLocationHelper?.setOnNewLocationListener(this@DeliverTrackingActivity)
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
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        latLng,
                                        DEFAULT_ZOOM.toFloat()
                                    )
                                )
                                isZoom = true
                                origin = latLng
                                destination = LatLng(userAddressItem?.lat!!, userAddressItem?.lng!!)
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
                GoogleDirection.withServerKey(serverKey)
                    .from(origin)
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
            try {
                val color = ContextCompat.getColor(mContext, R.color.color_yellow)
                if (direction.isOK) {
                    val route = withContext(Dispatchers.IO) {
                        direction.routeList[0]
                    }

                    val directionPositionList = route.legList[0].directionPoint
                    withContext(Dispatchers.Main) {
                        mMap.addMarker(MarkerOptions().position(origin!!).icon(currentIcon))
                        mMap.addMarker(
                            MarkerOptions().position(destination!!).icon(destinationIcon)
                        )
                        mMap.addPolyline(
                            DirectionConverter.createPolyline(
                                mContext,
                                directionPositionList,
                                5,
                                color
                            )
                        )
                        setCameraWithCoordinationBounds(route)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showSnackBar(direction.status)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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