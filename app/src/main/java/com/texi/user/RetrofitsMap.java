package com.texi.user;

import com.texi.user.DirectionData.Direction;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by AVJEET on 12-07-2017.
 */

public interface RetrofitsMap {
    @GET("/maps/api/directions/json?key=AIzaSyAwSn7f2pEMa0-ewnDZkeNa1U9oDBP0GaA")
    Call<Direction> getDirection(@Query("units") String units,
                                 @Query("origin") String originLatLng,
                                 @Query("destination") String destinationLatLng);

}
