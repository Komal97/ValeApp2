package com.texi.user;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.texi.user.DirectionData.Direction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AVJEET on 13-07-2017.
 */

public class RetrofitDirection {

    private final GoogleMap map;
    private Polyline line;

    RetrofitDirection(GoogleMap mMap){
        this.map=mMap;
    }

    String baseUrl = "https://maps.googleapis.com";

    Gson gson = new GsonBuilder().setLenient().create();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).build();
    RetrofitsMap retrofitsMap = retrofit.create(RetrofitsMap.class);
    private List<LatLng> list;


    public List<LatLng> get_direction_fetch_direction(LatLng originLatLng,LatLng destLatlng){
        String origlatLng = originLatLng.latitude+","+originLatLng.longitude;
        String destLtlng = destLatlng.latitude+","+destLatlng.longitude;

        Call<Direction> call = retrofitsMap.getDirection("metric",origlatLng,destLtlng);

        call.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {

                for(int i =0;i<response.body().getRoutes().size();i++){
                    Log.w("Url",call.request().url().toString());
                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                    list = decodePoly(encodedString);
                    if(line!=null) {
                        line.remove();
                    }

                    line  = map.addPolyline(new PolylineOptions().addAll(list).geodesic(true).width(20).color(RED));
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Log.w("Url",call.request().url().toString());
                Log.e("Failure",t.getLocalizedMessage());
            }
        });
        return list;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


}