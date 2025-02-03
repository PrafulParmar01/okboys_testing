package com.psp.google.direction.request;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;


public class DirectionDestinationRequest {
    private String apiKey;
    private LatLng origin;
    private List<LatLng> waypointList;

    public DirectionDestinationRequest(String apiKey, LatLng origin) {
        this.apiKey = apiKey;
        this.origin = origin;
    }


    public DirectionDestinationRequest and(LatLng waypoint) {
        if (waypointList == null) {
            waypointList = new ArrayList<>();
        }
        waypointList.add(waypoint);
        return this;
    }

    public DirectionDestinationRequest and(List<LatLng> waypointList) {
        if (this.waypointList == null) {
            this.waypointList = new ArrayList<>();
        }
        this.waypointList.addAll(waypointList);
        return this;
    }

    public DirectionRequest to(LatLng destination) {
        return new DirectionRequest(apiKey, origin, destination, waypointList);
    }
}
