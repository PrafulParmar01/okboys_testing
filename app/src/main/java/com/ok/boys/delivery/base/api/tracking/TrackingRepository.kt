package com.ok.boys.delivery.base.api.tracking

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ok.boys.delivery.base.api.network.TrackingNetworkModule
import com.ok.boys.delivery.base.api.tracking.model.TrackingUpdateResponse
import com.ok.boys.delivery.base.api.tracking.model.UpdateTrackingPositionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class TrackingRepository(val mContext: Context) {

    val trackingResponseLiveData = MutableLiveData<TrackingUpdateResponse>()
    val trackingErrorLiveData = MutableLiveData<String>()

    fun getUpdateTrackingPosition(request: UpdateTrackingPositionRequest){
        val api = TrackingNetworkModule().getInstance(mContext).create(TrackingRetrofitAPI::class.java)
        val call: Call<TrackingUpdateResponse> = api.getUpdateTrackingPosition(request)
        call.enqueue(object : Callback<TrackingUpdateResponse?> {
            override fun onResponse(call: Call<TrackingUpdateResponse?>, response: Response<TrackingUpdateResponse?>) {
                if (response.code() == 200) {
                    assert(response.body() != null)
                    trackingResponseLiveData.value = response.body()
                } else {
                    trackingErrorLiveData.value = "Something went wrong. Please try again"
                }
            }

            override fun onFailure(call: Call<TrackingUpdateResponse?>, t: Throwable) {
                t.printStackTrace()
                trackingErrorLiveData.value = "Something went wrong. Please try again"
            }
        })
    }
}
