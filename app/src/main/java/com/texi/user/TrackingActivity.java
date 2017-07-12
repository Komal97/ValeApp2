package com.texi.user;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techintegrity.appu.R;
import com.texi.user.gpsLocation.LocationAddress;
import com.texi.user.utils.Common;
import com.texi.user.utils.DistanceUtil;
import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Dialog dialog;
    RelativeLayout back;
    FragmentManager fm = getSupportFragmentManager();
    RelativeLayout call;
    ImageView sos;
    ImageView driverInfo;
    TextView contactText;
    RatingBar ratingBar;
    TextView rateMessage;
    String ratedValue;
    MarkerOptions marker;
    Dialog ProgressDialog;
    Button button;
    private Dialog driverInfoDialog;
    private Button submit;
    private LocationManager locationManager;
    private LatLng latLng;
    private LatLng latlngcenter;
    private Marker PickupMarker;


    RelativeLayout rlMainView;
    TextView tvTitle;
    private Typeface regularRoboto;
    private boolean dragMarker;
    private RotateLoading cusRotateLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        call = (RelativeLayout) findViewById(R.id.call);
        sos = (ImageView) findViewById(R.id.sos_call);
        button = (Button) findViewById(R.id.request_your_vehicle);
        back = (RelativeLayout) findViewById(R.id.layout_back_arrow);
        driverInfo = (ImageView) findViewById(R.id.driver_info);

        rlMainView = (RelativeLayout) findViewById(R.id.rlMainView);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");


        ProgressDialog = new Dialog(TrackingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);

        final Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + "+91 123456789"));
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(callIntent);
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(callIntent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(TrackingActivity.this);
            }
        });


        dialog = new Dialog(TrackingActivity.this);
        dialog.setContentView(R.layout.activity_feedback);

        ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        rateMessage = (TextView) dialog.findViewById(R.id.rateMessage);
        submit = (Button) dialog.findViewById(R.id.submit);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.yellow_texi), PorterDuff.Mode.SRC_ATOP);

        rateMessage.setText("Rate your Driver : 0");
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratedValue = String.valueOf(ratingBar.getRating());
                rateMessage.setText("Rate your Driver : " + ratedValue);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tH = new Intent(TrackingActivity.this, HomeActivity.class);
                tH.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(tH);
                finish();
            }
        });

        if (!getIntent().getBooleanExtra("paymentMade", false)) {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(TrackingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        latLng = new LatLng(latitude, longitude);

                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String str = addressList.get(0).getLocality() + ",";
                            str += addressList.get(0).getCountryName();
                            MarkerAdd(str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);

                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String str = addressList.get(0).getLocality() + ",";
                            str += addressList.get(0).getCountryName();
                            MarkerAdd(str);
                            // CameraPosition cp=CameraPosition.builder().target(new LatLng(28.63320831,77.22294813)).zoom(16).bearing(0).tilt(45).build();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

            }


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TrackingActivity.this, "Your Car is Parked", Toast.LENGTH_SHORT).show();
                    button.setVisibility(View.VISIBLE);
                    driverInfo.setVisibility(View.GONE);


                }


            }, 5000);
        } else {
            Toast.makeText(this, "Timer has Set", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //dialog.setTitle("Driver's Rating");
                    dialog.show();
                }
            }, 3000);
        }

        latlngcenter = Common.latlngcenter;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("distance", String.valueOf(DistanceUtil.distance(latLng, latlngcenter)));

                if (DistanceUtil.distance(latLng, latlngcenter) < 7) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 15000 * 60);
                    int minutes = calendar.get(Calendar.MINUTE);
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);

                    TimePickerDialog timeDialog = new TimePickerDialog(TrackingActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Log.w("Time Picked", "YOU PICKED " + i + " " + i1);
                            startActivity(new Intent(TrackingActivity.this, PaymentTypeActivity.class));
                        }
                    }, hours, minutes, false);


                    timeDialog.show();
                } else {
                    Common.showMKPanelError(TrackingActivity.this, "Delivery is available near PickUp Location Only", rlMainView, tvTitle, null);
                }
            }
        });


        driverInfoDialog = new Dialog(TrackingActivity.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        driverInfoDialog.setContentView(R.layout.driver_info);

        driverInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverInfoDialog.show();
            }
        });

        contactText = (TextView) driverInfoDialog.findViewById(R.id.phno);


        contactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calIntent = new Intent(Intent.ACTION_DIAL);
                calIntent.setData(Uri.parse("tel:" + "+911234567890"));
                startActivity(callIntent);
            }
        });


    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if (locationAddress != null) {
                if (locationAddress.equals("Unable connect to Geocoder")) {
                    Toast.makeText(TrackingActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                }
                LocationAddress.getAddressFromLocation(locationAddress, getApplicationContext(), new GeocoderHandlerLatitude());

            }

        }
    }


    private class GeocoderHandlerLatitude extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("locationAddress", "locationAddress = " + locationAddress);
            if (locationAddress != null) {
                if (locationAddress.equals("Unable connect to Geocoder")) {
                    Toast.makeText(TrackingActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                } else {
                    String[] LocationSplit = locationAddress.split("\\,");
                    Log.d("locationAddress", "locationAddress = " + locationAddress + "==" + Double.parseDouble(LocationSplit[0]) + "==" + Double.parseDouble(LocationSplit[1]));


                    latLng = new LatLng(Double.parseDouble(LocationSplit[0]), Double.parseDouble(LocationSplit[1]));
                    MarkerAdd(locationAddress);
                }
            } else {

                if (Common.isNetworkAvailable(TrackingActivity.this)) {
                    Log.d("locationAddress", "No Address Found = ");
                    Toast.makeText(TrackingActivity.this, "Location Not found", Toast.LENGTH_SHORT).show();
                } else {
                    Common.showInternetInfo(TrackingActivity.this, "");
                }
            }
        }
    }

    public void MarkerAdd(String title) {


        if (checkReady()) {

            if (marker != null)
                mMap.clear();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (latLng != null) {
                marker = new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_location_icon));
                PickupMarker = mMap.addMarker(marker);
                PickupMarker.setDraggable(true);
                builder.include(marker.getPosition());


            }


            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if (latLng != null) {
                LatLngBounds bounds = builder.build();

                //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300));
                if (areBoundsTooSmall(bounds, 300)) {
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 20);
                    mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -2.5);
                            mMap.animateCamera(zout);
                            if (PickupMarker != null)
                                BounceAnimationMarker(PickupMarker, latLng);

                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                } else {

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            mMap.animateCamera(zout);
                            BounceAnimationMarker(PickupMarker, latLng);

                        }

                        @Override
                        public void onCancel() {
//                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
//                            mMap.animateCamera(zout);
                        }
                    });

                }
            }


//            CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);
//            mMap.animateCamera(zoom);
            //mMap.moveCamera(cu);


            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                    dragMarker = true;


                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    Log.d("latitude", "latitude two= " + marker.getPosition().latitude);
                }

                @Override
                public void onMarkerDragEnd(Marker mrk) {

                    Log.d("latitude", "latitude three = " + mrk.getPosition().latitude + "==" + mrk.getPosition().longitude);
                    if (Common.isNetworkAvailable(TrackingActivity.this)) {

                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(mrk.getPosition().latitude, mrk.getPosition().longitude,
                                getApplicationContext(), new GeocoderHandler());

                    } else {
                        Toast.makeText(TrackingActivity.this, "No network", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.

        CameraPosition position = Common.mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Toast.makeText(this, "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);


            mMap.setMapType(Common.mgr.getSavedMapType());



        }

    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
