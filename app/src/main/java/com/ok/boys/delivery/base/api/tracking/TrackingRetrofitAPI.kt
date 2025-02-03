package com.ok.boys.delivery.base.api.tracking

import com.ok.boys.delivery.base.api.tracking.model.TrackingUpdateResponse
import com.ok.boys.delivery.base.api.tracking.model.UpdateTrackingPositionRequest
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TrackingRetrofitAPI {

    @POST(ApiConstant.API_UPDATE_TRACKING_POSSITION)
    fun getUpdateTrackingPosition(@Body request: UpdateTrackingPositionRequest): Call<TrackingUpdateResponse>
}