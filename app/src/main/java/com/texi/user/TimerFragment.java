package com.texi.user;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techintegrity.appu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends AppCompatActivity implements OnMapReadyCallback {
    RelativeLayout back;
    private static final String FORMAT = "%02d:%02d";
    private final long value_15 = 15;
    TextView tv;
    private final long MAX_TIME = 900000;
    private GoogleMap mMap;
    CountDownTimer timer;
    RelativeLayout call;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        tv = (TextView) findViewById(R.id.timer);
        call=(RelativeLayout) findViewById(R.id.call_button);
        back=(RelativeLayout) findViewById(R.id.layout_back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(TimerFragment.this)
                        .setTitle(R.string.c_t)
                        .setMessage(R.string.cancel_timer)
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                timer.cancel();
                                NavUtils.navigateUpFromSameTask(TimerFragment.this);
                            }
                        }).create().show();
            }
        });

        timer = new CountDownTimer(MAX_TIME, 1000) {
            @Override
            public void onTick(long l) {
                l = MAX_TIME - l;
                String ms = String.format(FORMAT, java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(l)+value_15, java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(l) - java.util.concurrent.TimeUnit.MINUTES.toSeconds(java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(l)));
                tv.setText(ms);

            }

            @Override
            public void onFinish() {
                tv.setText("Done!");

            }
        };
        timer.start();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callintent = new Intent(Intent.ACTION_DIAL);
                callintent.setData(Uri.parse("tel:" + "+91 123456789"));
                startActivity(callintent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
