package com.texi.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.techintegrity.appu.R;
import com.texi.user.DirectionData.Bounds;
import com.texi.user.gpsLocation.GPSTracker;
import com.texi.user.gpsLocation.LocationAddress;
import com.texi.user.utils.Common;
import com.texi.user.utils.MapStateManager;
import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TraceDriver extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    RelativeLayout back;
    RelativeLayout call;
    TextView ok;


    private LocationManager locationManager;

    private LatLng pickupLatLng;

    private int i = 0;
    private MarkerOptions markerOption;
    private Marker marker;
    private LatLng currentLoc;
    private LatLngBounds bounds;
    private Dialog ProgressDialog;
    private RotateLoading cusRotateLoading;
    private ArrayList<Marker> listMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_driver);

        back = (RelativeLayout) findViewById(R.id.layout_back_arrow);
        call = (RelativeLayout) findViewById(R.id.call_button);
        ok = (TextView) findViewById(R.id.ok_button);

        ProgressDialog = new Dialog(TraceDriver.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "+91 123456789"));
                startActivity(callIntent);

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(TraceDriver.this);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TraceDriver.this, TrackingActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(TraceDriver.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        mMap.setMapType(Common.mgr.getSavedMapType());
        pickupLatLng = new LatLng(getIntent().getDoubleExtra("pickuplat", 0), getIntent().getDoubleExtra("pickuplon", 0));

        RetrofitDirection direction = new RetrofitDirection(mMap, TraceDriver.this);
        GPSTracker gps = new GPSTracker(this);
        currentLoc = new LatLng(gps.getLatitude(), gps.getLongitude());

        direction.get_direction_fetch_direction(currentLoc, pickupLatLng);

        MarkerAdd(currentLoc);
        MarkerAdd(pickupLatLng);
    }

    public Marker MarkerAdd(LatLng latLng) {

        if (checkReady()) {

            if (latLng != null) {

                markerOption = new MarkerOptions()
                        .position(latLng)
                        .title("Marker")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_pin));
                marker = mMap.addMarker(markerOption);

            }

        }
        return marker;
    }


    public void BounceAnimationMarker(final Marker animationMarker, final LatLng animationLatLng) {
        if (animationLatLng != null) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(animationLatLng);
            startPoint.offset(0, -100);
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 1500;
            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * animationLatLng.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * animationLatLng.latitude + (1 - t) * startLatLng.latitude;
                    animationMarker.setPosition(new LatLng(lat, lng));
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Common.mgr = new MapStateManager(this);
        Common.mgr.saveMapState(mMap);

    }
}
