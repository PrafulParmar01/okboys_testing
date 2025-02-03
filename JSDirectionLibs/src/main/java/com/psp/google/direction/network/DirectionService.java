package com.psp.google.direction.network;

import com.psp.google.direction.constant.DirectionUrl;
import com.psp.google.direction.model.Direction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionService {

    @GET(DirectionUrl.DIRECTION_API_URL)
    Call<Direction> getDirection(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("waypoints") String waypoints,
                                 @Query("mode") String transportMode,
                                 @Query("departure_time") String departureTime,
                                 @Query("language") String language,
                                 @Query("units") String units,
                                 @Query("avoid") String avoid,
                                 @Query("transit_mode") String transitMode,
                                 @Query("alternatives") boolean alternatives,
                                 @Query("key") String apiKey);
}
