package com.ok.boys.delivery.services

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ok.boys.delivery.base.api.tracking.TrackingRepository
import com.ok.boys.delivery.base.api.tracking.model.UpdateTrackingPositionRequest
import com.ok.boys.delivery.base.locations.BaseLocationHelper
import com.ok.boys.delivery.receivers.NetworkConnectivityReceiver
import com.ok.boys.delivery.util.PrefUtil
import com.ok.boys.delivery.util.UtilsMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class LocationsService : LifecycleService(), BaseLocationHelper.NewLocationListener {

    private lateinit var mContext: Context
    private lateinit var notificationUtils: NotificationUtils
    private lateinit var baseLocationHelper: BaseLocationHelper
    private lateinit var trackingRepository: TrackingRepository
    private var latitude = 0.0
    private var longitude = 0.0

    companion object {


        fun getIntent(context: Context): Intent {
            return Intent(context, LocationsService::class.java)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        baseLocationHelper = BaseLocationHelper(this)
        baseLocationHelper.initLocation()

        notificationUtils = NotificationUtils(this)
        notificationUtils.startForegroundNotification()
        trackingRepository = TrackingRepository(mContext)
        Timber.e("LocationService:onCreate : ===> Done")

        trackingRepository.trackingResponseLiveData.observe(this) {
            Timber.e("trackingResponseLiveData: : ===> ${it.statusCode}")
        }

        trackingRepository.trackingErrorLiveData.observe(this) {
            Timber.e("trackingErrorLiveData: : ===> $it")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.e("LocationService:onStartCommand : ===> Done")
        onBackGroundDiscovery()
        return START_STICKY
    }


    private fun onBackGroundDiscovery() {
        baseLocationHelper.initLocation()
        baseLocationHelper.connectLocation()
        baseLocationHelper.setOnNewLocationListener(this)
    }


    private fun removeObserver() {
        if (::baseLocationHelper.isInitialized) {
            baseLocationHelper.disconnectLocation()
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.v("LocationService:onTaskRemoved : ===> Done")
    }

    override fun onNewLocation(locationResult: Location?, available: Boolean) {
        latitude = locationResult?.latitude as Double
        longitude = locationResult.longitude
        PrefUtil.putStringPref(PrefUtil.PREF_LATITUDE, latitude.toString(), mContext)
        PrefUtil.putStringPref(PrefUtil.PREF_LONGITUDE, longitude.toString(), mContext)
        Timber.e("CURRENT_LATITUDE : ===> $latitude")
        Timber.e("CURRENT_LONGITUDE : ===> $longitude")

        if (NetworkConnectivityReceiver().isInternetAvailable(applicationContext)) {
            if (latitude != 0.0 && longitude != 0.0) {
                val updateRequest = UpdateTrackingPositionRequest().apply {
                    latitude
                    longitude
                    deviceTime = UtilsMethod.getCurrentTimeDate()
                    fixTime = UtilsMethod.getCurrentTimeDate()
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        trackingRepository.getUpdateTrackingPosition(updateRequest)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Timber.e(e, "Error during tracking update.")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }
}