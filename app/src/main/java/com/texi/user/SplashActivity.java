package com.texi.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.texi.user.utils.Common;

import com.texi.user.utils.Url;
import com.techintegrity.appu.R;

public class SplashActivity extends AppCompatActivity {

    ImageView img_location;
    ImageView img_background;
    TextView location_text;
    ImageView img_back;

    SharedPreferences userPref;

    Common common = new Common();

    TranslateAnimation translation;
    private boolean gps_enabled;
    private LocationManager lm;
    private String flag="onCreate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img_location = (ImageView) findViewById(R.id.img_location);
        img_location.setVisibility(View.INVISIBLE);
        img_background = (ImageView) findViewById(R.id.img_background);
        img_back = (ImageView) findViewById(R.id.back_image);

        location_text = (TextView) findViewById(R.id.location_text);
        location_text.setVisibility(View.INVISIBLE);

        String token = FirebaseInstanceId.getInstance().getToken();
        Common.device_token = token;
        Log.d("device_token", "device_token = " + Common.device_token);

        userPref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("gps_enabled", "gps_enabled = " + gps_enabled);
                if (!gps_enabled) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                    dialog.setTitle("Improve location accurancy?");
                    dialog.setMessage("This app wants to change your device setting:");
                    dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            flag="onResume";
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            finish();
                        }
                    });
                    dialog.show();

                } else {

                    if (userPref.getString("isLogin", "").equals("1")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (Common.isNetworkAvailable(SplashActivity.this)) {
                                    String loginUrl = null;
                                    try {
                                        loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(userPref.getString("email", ""), "UTF-8") + "&password=" + userPref.getString("password", "");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("loginUrl", "loginUrl " + loginUrl);

                                    new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
                                } else {
                                    Common.showInternetInfo(SplashActivity.this, "");
                                }

                            }
                        }, 100);

                    } else {

                        startActivity(new Intent(SplashActivity.this, LoginOptionActivity.class));
                        finish();
                    }
                }
            }
        }, 4000);


    }

    public int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        if (gps_enabled&&flag=="onResume") {
            if (Common.device_token != null && Common.device_token.equals(""))
                Common.device_token = Common.device_refresh_token;

            if (Common.isNetworkAvailable(SplashActivity.this)) {

                String loginUrl = null;
                try {
                    loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(userPref.getString("email", ""), "UTF-8") + "&password=" + userPref.getString("password", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("loginUrl", "loginUrl " + loginUrl);
                new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
            } else {
                Common.showInternetInfo(SplashActivity.this, "");
            }
            startActivity(new Intent(SplashActivity.this, LoginOptionActivity.class));
            finish();
        }else if(flag=="onResume"){
            finish();
        }
    }

    private void startAnimation() {
        translation = new TranslateAnimation(0f, 0F, 0f, getDisplayHeight() * 0.70f);
        translation.setStartOffset(0);
        translation.setDuration(2500);
        translation.setFillAfter(true);
        translation.setInterpolator(new BounceInterpolator());
        translation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeout = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out);
                img_location.startAnimation(fadeout);
                location_text.startAnimation(fadeout);
                img_location.setVisibility(View.VISIBLE);
                location_text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img_background.startAnimation(translation);
    }
}