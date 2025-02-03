package com.psp.google.direction.request;

import com.google.android.gms.maps.model.LatLng;


public class DirectionOriginRequest {
    private String apiKey;

    public DirectionOriginRequest(String apiKey) {
        this.apiKey = apiKey;
    }

    public DirectionDestinationRequest from(LatLng origin) {
        return new DirectionDestinationRequest(apiKey, origin);
    }
}
