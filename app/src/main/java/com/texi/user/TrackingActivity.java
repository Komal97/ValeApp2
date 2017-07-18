package com.texi.user;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techintegrity.appu.R;
import com.texi.user.gpsLocation.GPSTracker;
import com.texi.user.utils.Common;
import com.texi.user.utils.DistanceUtil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.Calendar;

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
    Marker marker;
    Dialog ProgressDialog;
    Button button;
    private Dialog driverInfoDialog;
    private Button submit;


    RelativeLayout rlMainView;
    TextView tvTitle;
    private Typeface regularRoboto;
    private RotateLoading cusRotateLoading;
    private LatLng currentLoc;

    private LatLng pickupLatLng;
    private LatLngBounds bounds;
    private LatLng latLng;
    private MarkerOptions markerOption;
    private boolean warehouseflag = true;
    private ArrayList<Marker> listMarker;
    private GPSTracker gps;


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


        latLng = Common.latlngcenter;    //coordinate of warehouse

        pickupLatLng = new LatLng(getIntent().getDoubleExtra("pickuplat", 0), getIntent().getDoubleExtra("pickuplon", 0));


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


        //rating bar
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


            //when tracking acitvity is opened first time

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TrackingActivity.this, "Your Car is Parked", Toast.LENGTH_SHORT).show();
                    button.setVisibility(View.VISIBLE);
                    driverInfo.setVisibility(View.GONE);
                    listMarker.get(1).remove();
                }


            }, 5000);
        } else {

            // when u request ur valet
            warehouseflag = false;

            Toast.makeText(this, "Timer has Set", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //dialog.setTitle("Driver's Rating");
                    dialog.show();
                }
            }, 6000);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("distance", String.valueOf(DistanceUtil.distance(latLng, latLng)));
                gps = null;
                gps = new GPSTracker(TrackingActivity.this);
                currentLoc = new LatLng(gps.getLatitude(), gps.getLongitude());

                if (DistanceUtil.distance(currentLoc, latLng) < 5) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 15000 * 60);
                    int minutes = calendar.get(Calendar.MINUTE);
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);

                    TimePickerDialog timeDialog = new TimePickerDialog(TrackingActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Log.w("Time Picked", "YOU PICKED " + i + " " + i1);
                            startActivity(new Intent(TrackingActivity.this, PaymentTypeActivity.class));
                            finish();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        gps = new GPSTracker(this);
        currentLoc = new LatLng(gps.getLatitude(), gps.getLongitude());

        listMarker = new ArrayList<>();
        listMarker.add(MarkerAdd(currentLoc, "Current Location"));

        if (!warehouseflag) { //when valet is requested
            listMarker.add(MarkerAdd(latLng, "Driver"));
        }
        else{ // normal tracking
            listMarker.add(MarkerAdd(pickupLatLng, "Pickup Point"));
            listMarker.add(MarkerAdd(latLng, "WareHouse"));
        }





        bounds = MarkerBounds(listMarker);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, 300);

        mMap.moveCamera(cu);




    }

    public Marker MarkerAdd(LatLng latLng, String title) {
        if (latLng != null && title != "WareHouse") {

            markerOption = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_pin));
            marker = mMap.addMarker(markerOption);

        } else if (latLng != null) {
            markerOption = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
            marker = mMap.addMarker(markerOption);
        }
        return marker;
    }

    public LatLngBounds MarkerBounds(ArrayList<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        return bounds;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
