package com.psp.google.direction;

import com.psp.google.direction.request.DirectionOriginRequest;


public class GoogleDirection {
    public static DirectionOriginRequest withServerKey(String apiKey) {
        return new DirectionOriginRequest(apiKey);
    }
}
