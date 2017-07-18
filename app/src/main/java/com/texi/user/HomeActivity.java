package com.texi.user;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.techintegrity.appu.R;
import com.texi.user.adapter.CabDetailAdapter;
import com.texi.user.adapter.PickupDropLocationAdapter;
import com.texi.user.gpsLocation.GPSTracker;
import com.texi.user.gpsLocation.LocationAddress;
import com.texi.user.utils.CabDetails;
import com.texi.user.utils.Common;
import com.texi.user.utils.DistanceUtil;
import com.texi.user.utils.MapStateManager;
import com.twitter.sdk.android.core.services.params.Geocode;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback,/* CabDetailAdapter.OnCabDetailClickListener,*/ PickupDropLocationAdapter.OnDraoppickupClickListener {

    TextView txt_home, txt_reservation;
    RelativeLayout layout_slidemenu;
    EditText edt_pickup_location;
    EditText edt_drop_location;
    EditText edt_write_comment;
    LinearLayout layout_bottom;

    RelativeLayout layout_reservation;
    ImageView img_pickup_close;
    ImageView img_drop_close;
    RecyclerView recycle_pickup_location;
    RelativeLayout layout_pickup_drag_location;
    LinearLayout layout_no_result;
    TextView txt_not_found;
    TextView no_location;
    TextView please_check;

    RadioGroup radioGroup;

    SharedPreferences userPref;
    Button book_button;

    Typeface OpenSans_Regular, OpenSans_Bold, Roboto_Regular, Roboto_Medium, Roboto_Bold;



    GPSTracker gpsTracker;

    private GoogleMap googleMap;



    MarkerOptions marker;

    LatLng PickupLarLng;
    LatLng DropLarLng;
    double DropLongtude;
    double DropLatitude;
    double PickupLongtude;
    double PickupLatitude;

    ArrayList<HashMap<String, String>> locationArray;
    private ArrayList<LatLng> arrayPoints = null;

    Dialog NowDialog;
    Dialog ReservationDialog;
    CabDetailAdapter cabDetailAdapter;

    TextView txt_car_header;

    TextView txt_car_descriptin;
    TextView txt_first_price;
    TextView txt_first_km;
    TextView txt_sec_pric;
    TextView txt_sec_km;
    TextView txt_thd_price, txt_locatons;
    RelativeLayout layout_one;
    RelativeLayout layout_two;
    RelativeLayout layout_three;

    String SelectedDate = "";


    SlidingMenu slidingMenu;


    float distance = 0;

    float totlePrice = 0;


    String bothLocationString = "";


    PickupDropLocationAdapter pickupDropLocationAdapter;

    LinearLayoutManager pickupDragLayoutManager;

    boolean ClickOkButton = false;

    Calendar myCalendar;
    Calendar TimeCalendar;
    TextView txt_date;

    DatePickerDialog.OnDateSetListener date;
    String BookingDateTime = "";
    SimpleDateFormat bookingFormate;
    int devise_width;



    Common common = new Common();

    boolean LocationDistanse = false;
    Marker PickupMarker;
    Marker DropMarker;

    String BookingType;

    ArrayList<HashMap<String, String>> FixRateArray;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;


    String LanguageCode;
    boolean dragMarker = false;
    private EditText car_model;
    private EditText car_number;
    private Button submitButton;
    private Button cancelButton;
    private Dialog inputDialog;
    private static LatLng latlngcenter;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LanguageCode = Locale.getDefault().getLanguage();
        Log.d("LanguageCode", "LanguageCode = " + LanguageCode);

        userPref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);


        latlngcenter = Common.latlngcenter;


        txt_home = (TextView) findViewById(R.id.txt_home);
        layout_slidemenu = (RelativeLayout) findViewById(R.id.layout_slidemenu);
        edt_pickup_location = (EditText) findViewById(R.id.edt_pickup_location);
        edt_drop_location = (EditText) findViewById(R.id.edt_drop_location);
        edt_write_comment = (EditText) findViewById(R.id.edt_write_comment);
        book_button = (Button) findViewById(R.id.book_button);


        layout_reservation = (RelativeLayout) findViewById(R.id.layout_reservation);
        img_pickup_close = (ImageView) findViewById(R.id.img_pickup_close);
        img_drop_close = (ImageView) findViewById(R.id.img_drop_close);
        recycle_pickup_location = (RecyclerView) findViewById(R.id.recycle_pickup_location);
        layout_pickup_drag_location = (RelativeLayout) findViewById(R.id.layout_pickup_drag_location);
        layout_no_result = (LinearLayout) findViewById(R.id.layout_no_result);
        txt_not_found = (TextView) findViewById(R.id.txt_not_found);
        no_location = (TextView) findViewById(R.id.no_location);
        please_check = (TextView) findViewById(R.id.please_check);
        txt_locatons = (TextView) findViewById(R.id.txt_locatons);
        txt_reservation = (TextView) findViewById(R.id.txt_reservation);

        layout_bottom = (LinearLayout) findViewById(R.id.layout_footer);

        String bookinCancel = getIntent().getStringExtra("cancel_booking");
        if (bookinCancel != null && bookinCancel.equals("1")) {
            Common.showMkSucess(HomeActivity.this, getResources().getString(R.string.your_booking_cancel), "yes");
        }

        ProgressDialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);
//        Log.d("device_token","device_token = "+Common.device_token);
//        Log.d("id_device_token","id_device_token = "+userPref.getString("id_device_token",""));

        if (Common.device_token != null && !Common.device_token.equals(""))
            new Common.CallUnSubscribeTaken(HomeActivity.this, Common.device_token).execute();
        else if (Common.device_refresh_token != null && !Common.device_refresh_token.equals(""))
            new Common.CallUnSubscribeTaken(HomeActivity.this, Common.device_refresh_token).execute();

        arrayPoints = new ArrayList<LatLng>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        devise_width = displaymetrics.widthPixels;

//        layout_now.getLayoutParams().width = (int) (devise_width * 0.50);
//        RelativeLayout.LayoutParams resParam = new RelativeLayout.LayoutParams((int) (devise_width * 0.51), ViewGroup.LayoutParams.WRAP_CONTENT);
//        resParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        resParam.addRule(RelativeLayout.ALIGN_PARENT_END);
//
//        layout_reservation.setLayoutParams(resParam);

        bookingFormate = new SimpleDateFormat("h:mm a, d, MMM yyyy,EEE");

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_home.setTypeface(OpenSans_Bold);
        txt_not_found.setTypeface(OpenSans_Bold);
        txt_locatons.setTypeface(Roboto_Bold);
        txt_reservation.setTypeface(Roboto_Bold);


        edt_pickup_location.setTypeface(OpenSans_Regular);
        edt_drop_location.setTypeface(OpenSans_Regular);
        edt_write_comment.setTypeface(Roboto_Regular);
        no_location.setTypeface(Roboto_Regular);
        please_check.setTypeface(Roboto_Regular);

        pickupDragLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recycle_pickup_location.setLayoutManager(pickupDragLayoutManager);

        /*get Current Location And Set Edittext*/
        PickupLatitude = getIntent().getDoubleExtra("PickupLatitude", 0.0);
        PickupLongtude = getIntent().getDoubleExtra("PickupLongtude", 0.0);

        gpsTracker = new GPSTracker(HomeActivity.this);

        if (PickupLongtude != 0.0 && PickupLatitude != 0.0) {
            bothLocationString = "pickeup";
            if (Common.isNetworkAvailable(HomeActivity.this)) {
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(PickupLatitude, PickupLongtude,
                        getApplicationContext(), new GeocoderHandler());

                PickupLarLng = new LatLng(PickupLatitude, PickupLongtude);
                ClickOkButton = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MarkerAdd();
                    }
                }, 1000);
            } else {
                Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
            }
        } else {

            if (gpsTracker.checkLocationPermission()) {

                PickupLatitude = gpsTracker.getLatitude();
                PickupLongtude = gpsTracker.getLongitude();
                PickupLarLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                ClickOkButton = true;

                bothLocationString = "pickeup";
                if (Common.isNetworkAvailable(HomeActivity.this)) {
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(PickupLatitude, PickupLongtude,
                            getApplicationContext(), new GeocoderHandler());

                    PickupLarLng = new LatLng(PickupLatitude, PickupLongtude);
                    ClickOkButton = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MarkerAdd();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                }

            } else {
                gpsTracker.showSettingsAlert();
            }
        }

        Log.d("gpsTracker", "gpsTracker =" + gpsTracker.canGetLocation() + "==" + gpsTracker.checkLocationPermission());

        /*Pickup Location autocomplate start*/
        //LocationAutocompleate(edt_pickup_location, "pickeup");
        EditorActionListener(edt_pickup_location, "pickeup");
        AddTextChangeListener(edt_pickup_location, "pickeup");
        AddSetOnClickListener(edt_pickup_location, "pickeup");
        /*Pickup Location autocomplate end*/

        /*Drop Location autocomplate start*/
        //LocationAutocompleate(edt_drop_location, "drop");
        EditorActionListener(edt_drop_location, "drop");
        AddTextChangeListener(edt_drop_location, "drop");
        AddSetOnClickListener(edt_drop_location, "drop");
        /*Drop Location autocomplate end*/

        /*Slide Menu Start*/

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu, HomeActivity.this, "book my trip");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        /*Slide Menu End*/

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        NowDialog = new Dialog(HomeActivity.this, R.style.DialogUpDownAnim);
        NowDialog.setContentView(R.layout.now_dialog_layout);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            NowDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        NowDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (layout_reservation.getVisibility() == View.GONE)
                    layout_reservation.setVisibility(View.VISIBLE);
            }
        });


        radioGroup = (RadioGroup) NowDialog.findViewById(R.id.radio_group);



        final RelativeLayout layout_book = (RelativeLayout) NowDialog.findViewById(R.id.layout_book);
        layout_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NowDialog.cancel();

                layout_reservation.setVisibility(View.VISIBLE);

                Log.d("total price", "total price = " + totlePrice);


                AlertDialog.Builder book = new AlertDialog.Builder(HomeActivity.this).setView(R.layout.booking_succesful);
                final AlertDialog booking = book.create();


                booking.show();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        booking.dismiss();

                        Intent intent = new Intent(HomeActivity.this,TraceDriver.class);
                        intent.putExtra("pickuplat",PickupLatitude);
                        intent.putExtra("pickuplon",PickupLongtude);
                        startActivity(intent);
                    }
                }, 3000);


            }
        });

        RelativeLayout layout_cancle = (RelativeLayout) NowDialog.findViewById(R.id.layout_cancle);
        layout_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowDialog.cancel();


            }
        });

        inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.activity_dialog_input_info);
        inputDialog.setTitle("Car Info");
        inputDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.white));

        inputDialog.create();

        car_model = (EditText) inputDialog.findViewById(R.id.car_model);
        car_number = (EditText) inputDialog.findViewById(R.id.car_number);

        submitButton = (Button) inputDialog.findViewById(R.id.submit);
        cancelButton = (Button) inputDialog.findViewById(R.id.cancel);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_model.getText().toString().trim().equals("") || car_number.getText().toString().equals("")) {
                    Common.showMkError(HomeActivity.this, "23");
                } else {
                    inputDialog.dismiss();
                    NowDialog.show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog.dismiss();
            }
        });

        book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("length ", "length = " + edt_pickup_location.getText().toString().length());



                if (edt_pickup_location.getText().toString().length() == 0 || DistanceUtil.distance(PickupLarLng,latlngcenter)==-1) {
                    Common.showMkError(HomeActivity.this, getResources().getString(R.string.enter_pickup));
                    return;
                } else if (DistanceUtil.distance(PickupLarLng, latlngcenter) > Double.parseDouble("7")) {
                    Common.showMkError(HomeActivity.this, getString(R.string.not_available));
                    return;
                }


                BookingDateTime = bookingFormate.format(Calendar.getInstance().getTime());

                BookingType = "Now";

                Log.w("Alert", "Command Exectuted");


                // NowDialog.show();
                inputDialog.show();


            }
        });


        myCalendar = Calendar.getInstance();
        TimeCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SelectedDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                updateLabel();
            }

        };



        /*Reservation Image Click popup end*/

        img_pickup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_pickup_location.setText("");
                PickupLarLng = null;
                PickupLatitude = 0.0;
                PickupLongtude = 0.0;
                MarkerAdd();
            }
        });

        img_drop_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_drop_location.setText("");
                DropLarLng = null;
                DropLongtude = 0.0;
                DropLatitude = 0.0;
                MarkerAdd();
            }
        });

    }


    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        //Toast.makeText(HomeActivity.this,"Date = "+sdf.format(myCalendar.getTime()),Toast.LENGTH_LONG).show();
        txt_date.setText(sdf.format(myCalendar.getTime()));

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        Log.d("Map Ready", "Map Ready" + gpsTracker.getLatitude() + "==" + gpsTracker.getLongitude());
    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (googleMap == null) {
            Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void CaculationDirationIon() {
        String CaculationLocUrl = "";
//        try {
//            DrowLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin="+URLEncoder.encode(edt_pickup_location.getText().toString(), "UTF-8")+"&destination="+URLEncoder.encode(edt_drop_location.getText().toString(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        CaculationLocUrl = "http://maps.googleapis.com/maps/api/directions/json?sensor=true&mode=driving&origin=" + PickupLatitude + "," + PickupLongtude + "&destination=" + DropLatitude + "," + DropLongtude;
        Log.d("CaculationLocUrl", "CaculationLocUrl = " + CaculationLocUrl);
        Ion.with(HomeActivity.this)
                .load(CaculationLocUrl)
                .setTimeout(60 * 60 * 1000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        Log.d("Login result", "Login result = " + result + "==" + error);
                        if (error == null) {
                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("status").toLowerCase().equals("ok")) {


                                    JSONArray routArray = new JSONArray(resObj.getString("routes"));
                                    JSONObject routObj = routArray.getJSONObject(0);
                                    Log.d("geoObj", "DrowLocUrl geoObj one= " + routObj);
                                    JSONArray legsArray = new JSONArray(routObj.getString("legs"));
                                    JSONObject legsObj = legsArray.getJSONObject(0);

                                    JSONObject disObj = new JSONObject(legsObj.getString("distance"));
                                    //if (disObj.getInt("value") > 1000)
                                    distance = (float) disObj.getInt("value") / 1000;
//                                    else if (disObj.getInt("value") > 100)
//                                        distance = (float) disObj.getInt("value") / 100;
//                                    else if (disObj.getInt("value") > 10)
//                                        distance = (float) disObj.getInt("value") / 10;
//                                    else if(disObj.getInt("value") == 0)
//                                        distance = (float) disObj.getInt("value");
                                    Log.d("distance", "distance = " + distance);
                                    Log.d("dis", "dis = " + distance);

//                                    JSONObject duration = new JSONObject(legsObj.getString("duration"));
//
//                                    AstTime = duration.getString("text");
//                                    String[] durTextSpi = AstTime.split(" ");
//                                    Log.d("durTextSpi", "min  = durTextSpi = " + durTextSpi.length);
//                                    int hours = 0;
//                                    int mintus = 0;
//                                    if (durTextSpi.length == 4) {
//                                        hours = Integer.parseInt(durTextSpi[0]) * 60;
//                                        mintus = Integer.parseInt(durTextSpi[2]);
//                                    } else if (durTextSpi.length == 2) {
//                                        if (durTextSpi[1].contains("mins"))
//                                            mintus = Integer.parseInt(durTextSpi[0]);
//                                        else
//                                            mintus = Integer.parseInt(durTextSpi[0]);
//                                    }
//                                    Log.d("hours", "hours = " + hours + "==" + mintus);
//                                    totalTime = mintus + hours;
//
//                                    googleDuration = duration.getInt("value");
//
//
//                                    if (FixRateArray.size() > 0) {
//                                        for (int ci = 0; ci < cabDetailArray.size(); ci++) {
//
//                                            CabDetails FixCabDetails = cabDetailArray.get(ci);
//
//                                            for (int fi = 0; fi < FixRateArray.size(); fi++) {
//                                                HashMap<String, String> FixHasMap = FixRateArray.get(fi);
//
//                                                Log.d("car_type_id", "car_type_id = " + FixHasMap.get("car_type_id") + "==" + FixCabDetails.getId());
//                                                if (FixHasMap.get("car_type_id").equals(FixCabDetails.getId())) {
//                                                    CabDetails cabDetails = cabDetailArray.get(ci);
//                                                    cabDetails.setFixPrice(FixHasMap.get("fix_price").toString());
//                                                    cabDetails.setAreaId(FixHasMap.get("area_id").toString());
//                                                    break;
//                                                }
//
//                                                Log.d("fi", "car_type_id fi = " + fi);
//                                            }
//                                            Log.d("ci", "car_type_id ci = " + ci);
//                                        }
//                                    } else {
//                                        for (int ci = 0; ci < cabDetailArray.size(); ci++) {
//                                            CabDetails AllCabDetails = cabDetailArray.get(ci);
//                                            AllCabDetails.setFixPrice("");
//                                            AllCabDetails.setAreaId("");
//                                        }
//                                    }
//                                    if (cabDetailArray.size() > 0) {
//                                        CabDetails cabDetails = cabDetailArray.get(0);
//                                        if (!cabDetails.getFixPrice().equals("")) {
//                                            layout_timming.setVisibility(View.GONE);
//                                            layout_far_breakup.setVisibility(View.INVISIBLE);
//                                            totlePrice = Float.parseFloat(cabDetails.getFixPrice());
//                                            txt_total_price.setText(Math.round(totlePrice) + "");
//                                        } else {
//                                            Log.d("fromintailrate", "fromintailrate = " + car_rate + "==" + FirstKm + "==" + distance + "==" + fromintailrate + "==" + ride_time_rate + "==" + totalTime);
//                                            if (car_rate != null && fromintailrate != null && ride_time_rate != null)
//                                                totlePrice = Common.getTotalPrice(car_rate, FirstKm, distance, fromintailrate, ride_time_rate, totalTime);
//                                            else
//                                                totlePrice = 0f;
//
//                                            Log.d("totlePrice", "totlePrice = " + totlePrice);
//
//                                            layout_timming.setVisibility(View.VISIBLE);
//                                            layout_far_breakup.setVisibility(View.VISIBLE);
//                                            txt_total_price.setText(Math.round(totlePrice) + "");
//                                        }

                                    //  }
                                    LocationDistanse = true;


                                } else {
                                    LocationDistanse = false;
                                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
                });

        if (!dragMarker) {
            dragMarker = false;
            MarkerAdd();
        }


    }

    public void GetLatLongFromAddress(String address, String StrValText) {

        String GetLatLonLocUrl = null;
        try {
            GetLatLonLocUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("CaculationLocUrl", "CaculationLocUrl = " + GetLatLonLocUrl);
        Ion.with(HomeActivity.this)
                .load(GetLatLonLocUrl)
                .setTimeout(60 * 60 * 1000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        Log.d("Login result", "Login result = " + result + "==" + error);
                        if (error == null) {
                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("status").toLowerCase().equals("ok")) {
                                    JSONArray routArray = new JSONArray(resObj.getString("results"));
                                    JSONObject routObj = routArray.getJSONObject(0);
                                    JSONObject geometryObj = new JSONObject(routObj.getString("geometry"));
                                    JSONObject locationObj = new JSONObject(geometryObj.getString("location"));

                                    Log.d("routArray", "routArray " + locationObj);

                                    if (bothLocationString.equals("pickeup")) {
                                        PickupLatitude = Double.parseDouble(locationObj.getString("lat"));
                                        PickupLongtude = Double.parseDouble(locationObj.getString("lng"));
                                        PickupLarLng = new LatLng(Double.parseDouble(locationObj.getString("lat")), Double.parseDouble(locationObj.getString("lng")));
                                    } else if (bothLocationString.equals("drop")) {
                                        DropLongtude = Double.parseDouble(locationObj.getString("lng"));
                                        DropLatitude = Double.parseDouble(locationObj.getString("lat"));
                                        DropLarLng = new LatLng(Double.parseDouble(locationObj.getString("lat")), Double.parseDouble(locationObj.getString("lng")));
                                    }

                                    if (edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                                        if (checkReady() && Common.isNetworkAvailable(HomeActivity.this)) {
                                            PickupFixRateCall();
                                        } else {
                                            Common.showInternetInfo(HomeActivity.this, "");
                                        }
                                    } else {
                                        MarkerAdd();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
                });


    }




    public void PickupFixRateCall() {

//        progressDialog.show();
//        cusRotateLoading.start();

        FixRateArray = new ArrayList<>();

//        String FixAreaUrl = Url.FixAreaUrl+"?pick_lat="+PickupLatitude+"&pick_long="+PickupLongtude+"&drop_lat="+DropLatitude+"&drop_long="+DropLongtude;
//        Log.d("FixAreaUrl","FixAreaUrl ="+FixAreaUrl);
//        Ion.with(HomeActivity.this)
//                .load(FixAreaUrl)
//                .setTimeout(60*60*1000)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception error, JsonObject result) {
//                        // do stuff with the result or error
//                        Log.d("load_trips result", "load_trips result = " + result + "==" + error);
//                        if (error == null) {

//                            try {
//                                JSONObject resObj = new JSONObject(result.toString());
//                                Log.d("loadTripsUrl", "loadTripsUrl two= " + resObj);
//                                if(resObj.getString("success").equals("true")) {
//                                    JSONArray fixAreaArray = new JSONArray(resObj.getString("fixAreaPriceList"));
//                                    for (int fi = 0; fi < fixAreaArray.length(); fi++) {
//                                        JSONObject fixAreaObj = fixAreaArray.getJSONObject(fi);
//
//                                        Log.d("FixRateArray", "FixAreaUrl FixRateArray = " + fixAreaObj);
//                                        if (!fixAreaObj.getString("fix_price").equals("0")) {
//                                            HashMap<String, String> FixHasMap = new HashMap<String, String>();
//                                            FixHasMap.put("fix_price", fixAreaObj.getString("fix_price").toString());
//                                            FixHasMap.put("car_type_id", fixAreaObj.getString("car_type_id").toString());
//                                            FixHasMap.put("car_type_name", fixAreaObj.getString("car_type_name").toString());
//                                            FixHasMap.put("area_title", fixAreaObj.getString("area_title").toString());
//                                            FixHasMap.put("area_id", fixAreaObj.getString("area_id").toString());
//                                            FixRateArray.add(FixHasMap);
//                                        }
//                                    }
//                                }
//                                Log.d("FixRateArray","FixAreaUrl FixRateArray = "+FixRateArray.size());
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
        CaculationDirationIon();

//                        } else {
//                            progressDialog.cancel();
//                            cusRotateLoading.stop();
//
//                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
//                        }
//                    }
//                });

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
                    Toast.makeText(HomeActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("locationAddress", "locationAddress = " + locationAddress + "==" + bothLocationString + "==" + dragMarker);
                    if (bothLocationString.equals("pickeup") && edt_pickup_location != null)
                        edt_pickup_location.setText(locationAddress);

                    LocationAddress.getAddressFromLocation(locationAddress, getApplicationContext(), new GeocoderHandlerLatitude());

                }

            } else {
                NowDialog.cancel();
                layout_reservation.setVisibility(View.VISIBLE);
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();

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
                    Toast.makeText(HomeActivity.this, "No Network conection", Toast.LENGTH_LONG).show();
                } else {
                    String[] LocationSplit = locationAddress.split("\\,");
                    Log.d("locationAddress", "locationAddress = " + locationAddress + "==" + Double.parseDouble(LocationSplit[0]) + "==" + Double.parseDouble(LocationSplit[1]));
                    if (bothLocationString.equals("pickeup")) {
                        PickupLatitude = Double.parseDouble(LocationSplit[0]);
                        PickupLongtude = Double.parseDouble(LocationSplit[1]);
                        PickupLarLng = new LatLng(Double.parseDouble(LocationSplit[0]), Double.parseDouble(LocationSplit[1]));
                    }


                    if (edt_drop_location != null && edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                        if (checkReady() && Common.isNetworkAvailable(HomeActivity.this)) {
                            PickupFixRateCall();
                        } else {
                            Common.showInternetInfo(HomeActivity.this, "");
                        }
                    } else {
                        MarkerAdd();
                    }
                }
            } else {

                if (Common.isNetworkAvailable(HomeActivity.this)) {
                    Log.d("locationAddress", "No Address Found = ");
                    ProgressDialog.show();
                    cusRotateLoading.start();
                    if (bothLocationString.equals("pickeup")) {
                        GetLatLongFromAddress(edt_pickup_location.getText().toString(), bothLocationString);
                    } else if (bothLocationString.equals("drop")) {
                        GetLatLongFromAddress(edt_drop_location.getText().toString(), bothLocationString);
                    }
                } else {
                    Common.showInternetInfo(HomeActivity.this, "");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        common.SlideMenuDesign(slidingMenu, HomeActivity.this, "home");
    }

    /*Add marker function*/
    public void MarkerAdd() {

        if (checkReady()) {

            if (marker != null)
                googleMap.clear();



            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (PickupLarLng != null) {
                marker = new MarkerOptions()
                        .position(PickupLarLng)
                        .title("Pick Up Location")

                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_pin));
                PickupMarker = googleMap.addMarker(marker);
                PickupMarker.setDraggable(true);
                builder.include(marker.getPosition());


            }

            if (DropLarLng != null) {
                marker = new MarkerOptions()
                        .position(DropLarLng)
                        .title("Drop Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_location_icon));

                DropMarker = googleMap.addMarker(marker);
                DropMarker.setDraggable(true);
                builder.include(marker.getPosition());
            }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if (PickupLarLng != null) {
                LatLngBounds bounds = builder.build();

                //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300));
                if (areBoundsTooSmall(bounds, 300)) {
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 20);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -2.5);
                            googleMap.animateCamera(zout);
                            if (PickupMarker != null)
                                BounceAnimationMarker(PickupMarker, PickupLarLng);
                            if (DropMarker != null)
                                BounceAnimationMarker(DropMarker, DropLarLng);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                } else {

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            googleMap.animateCamera(zout);
                            BounceAnimationMarker(PickupMarker, PickupLarLng);
                            BounceAnimationMarker(DropMarker, DropLarLng);
                        }

                        @Override
                        public void onCancel() {
//                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
//                            googleMap.animateCamera(zout);
                        }
                    });

                }

            }


//            CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);
//            googleMap.animateCamera(zoom);
            //googleMap.moveCamera(cu);



            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker pickMarker) {

                    Log.d("bothLocationString", "bothLocationString pickup= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_pickup_location.getText().toString());
                    if (marker.getTitle().equals("Pick Up Location"))
                        bothLocationString = "pickeup";

                    Log.d("bothLocationString", "bothLocationString pickup= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_pickup_location.getText().toString());
                    Log.d("bothLocationString", "bothLocationString drop= " + bothLocationString + "==" + marker.getTitle() + "==" + edt_drop_location.getText().toString());

                    return false;
                }
            });


            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                    dragMarker = true;

                    if (marker.getTitle().equals("Pick Up Location"))
                        bothLocationString = "pickeup";

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    Log.d("latitude", "latitude two= " + marker.getPosition().latitude);
                }

                @Override
                public void onMarkerDragEnd(Marker mrk) {

                    Log.d("latitude", "latitude three = " + mrk.getPosition().latitude + "==" + mrk.getPosition().longitude);
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        ClickOkButton = true;
                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocation(mrk.getPosition().latitude, mrk.getPosition().longitude,
                                getApplicationContext(), new GeocoderHandler());

                    } else {
                        Toast.makeText(HomeActivity.this, "No network", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    public void BounceAnimationMarker(final Marker animationMarker, final LatLng animationLatLng) {
        if (animationLatLng != null) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = googleMap.getProjection();
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

    public void EditorActionListener(final EditText locationEditext, final String clickText) {

        locationEditext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.d("Edit text", "Edit text = " + v.getText().toString());

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    Log.d("locationEditext", "locationEditext = " + locationEditext.getText().toString());
                    if (locationEditext.getText().toString().length() > 0) {

                        if (clickText.equals("pickeup")) {
                            if (Common.isNetworkAvailable(HomeActivity.this)) {
                                bothLocationString = "pickeup";
                                LocationAddress.getAddressFromLocation(edt_pickup_location.getText().toString(), getApplicationContext(), new GeocoderHandlerLatitude());
                            } else {
                                Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                            }
                        }

                        layout_pickup_drag_location.setVisibility(View.GONE);
                        if (edt_drop_location.getText().length() > 0 && edt_pickup_location.getText().length() > 0) {
                            if (checkReady() && Common.isNetworkAvailable(HomeActivity.this)) {
                                //new CaculationDiration().execute();
                                //CaculationDirationIon();
                            } else {
                                Common.showInternetInfo(HomeActivity.this, "");
                            }
                        }

                    } else {
                        PickupLarLng = null;
                        PickupLatitude = 0.0;
                        PickupLongtude = 0.0;
                        Toast.makeText(HomeActivity.this, "Please Enter Location", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
    }

    public void AddTextChangeListener(final EditText locationEditext, final String clickText) {
        locationEditext.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.d("clickText", "clickText = " + clickText);
                if (s.length() != 0) {

                    if (clickText.equals("drop")) {
                        img_drop_close.setVisibility(View.VISIBLE);
                    } else if (clickText.equals("pickeup")) {
                        img_pickup_close.setVisibility(View.VISIBLE);
                    }
                    Log.d("ClickOkButton", "ClickOkButton = " + ClickOkButton);
                    if (!ClickOkButton) {
                        layout_pickup_drag_location.setVisibility(View.VISIBLE);
                        Log.d("ClickOkButton", "ClickOkButton = " + s.toString());
                        //new getPickupDropAddress(s.toString()).execute();
                        getPickupDropAddressIon(s.toString());
                    }
                } else {
                    if (clickText.equals("drop")) {
                        img_drop_close.setVisibility(View.GONE);
                        DropLarLng = null;
                        DropLongtude = 0.0;
                        DropLatitude = 0.0;
                    } else if (clickText.equals("pickeup")) {
                        img_pickup_close.setVisibility(View.GONE);
                        PickupLarLng = null;
                        PickupLatitude = 0.0;
                        PickupLongtude = 0.0;
                    }
                    layout_pickup_drag_location.setVisibility(View.GONE);

                    MarkerAdd();
                }

            }
        });

    }

    public void AddSetOnClickListener(EditText locationEditext, final String ClickValue) {

        locationEditext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClickOkButton = false;
                bothLocationString = ClickValue;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (ClickValue.equals("drop")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_175), 0, 0);
                } else if (ClickValue.equals("pickeup")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_130), 0, 0);
                }
                layout_pickup_drag_location.setLayoutParams(params);
                return false;
            }
        });

        locationEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOkButton = false;
                bothLocationString = ClickValue;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (ClickValue.equals("drop")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_175), 0, 0);
                } else if (ClickValue.equals("pickeup")) {
                    params.setMargins(0, (int) getResources().getDimension(R.dimen.height_130), 0, 0);
                }
                layout_pickup_drag_location.setLayoutParams(params);
            }
        });
    }


    public void getPickupDropAddressIon(String inputSting) {
        String locatinUrl = "";
        locationArray = new ArrayList<>();
        try {
            locatinUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyClAKS0xlhMe97OHv9mU7OPNekPtA6ADVM&input=" + URLEncoder.encode(inputSting, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("locatinUrl", "Login locatinUrl = " + locatinUrl);
        Ion.with(HomeActivity.this)
                .load(locatinUrl)
                .setTimeout(60 * 60 * 1000)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("Login result", "Login result = " + result + "==" + error);

                        if (error == null) {
                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("status").toLowerCase().equals("ok")) {
                                    JSONArray predsJsonArray = resObj.getJSONArray("predictions");
                                    for (int i = 0; i < predsJsonArray.length(); i++) {
                                        HashMap<String, String> locHashMap = new HashMap<String, String>();
                                        locHashMap.put("location name", predsJsonArray.getJSONObject(i).getString("description"));
                                        locationArray.add(locHashMap);
                                    }

                                    if (locationArray != null && locationArray.size() > 0) {
                                        recycle_pickup_location.setVisibility(View.VISIBLE);
                                        layout_no_result.setVisibility(View.GONE);
                                        pickupDropLocationAdapter = new PickupDropLocationAdapter(HomeActivity.this, locationArray);
                                        recycle_pickup_location.setAdapter(pickupDropLocationAdapter);
                                        pickupDropLocationAdapter.setOnDropPickupClickListener(HomeActivity.this);
                                        pickupDropLocationAdapter.updateItems();
                                    }

                                    Log.d("locationArray", "locationArray = " + locationArray.size());
                                } else if (resObj.getString("status").equals("ZERO_RESULTS")) {
                                    if (locationArray != null && locationArray.size() > 0)
                                        locationArray.clear();

                                    layout_no_result.setVisibility(View.VISIBLE);
                                    recycle_pickup_location.setVisibility(View.GONE);

                                    Log.d("locationArray", "locationArray = " + locationArray.size());
                                    if (pickupDropLocationAdapter != null)
                                        pickupDropLocationAdapter.updateBlankItems(locationArray);
                                } else if (resObj.getString("status").equals("OVER_QUERY_LIMIT")) {
                                    if (locationArray != null && locationArray.size() > 0)
                                        locationArray.clear();

                                    layout_no_result.setVisibility(View.GONE);
                                    recycle_pickup_location.setVisibility(View.GONE);

                                    Log.d("locationArray", "locationArray = " + locationArray.size());
                                    if (pickupDropLocationAdapter != null)
                                        pickupDropLocationAdapter.updateBlankItems(locationArray);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Common.ShowHttpErrorMessage(HomeActivity.this, error.getMessage());
                        }
                    }
                });
    }

    @Override
    public void PickupDropClick(int position) {

        Log.d("bothLocationString", "bothLocationString = " + locationArray.size());
        if (locationArray != null && locationArray.size() > 0) {
            HashMap<String, String> picDrpHash = locationArray.get(position);
            Log.d("bothLocationString", "bothLocationString = " + bothLocationString);
            if (!bothLocationString.equals("")) {
                if (bothLocationString.equals("pickeup")) {
                    edt_pickup_location.setText(picDrpHash.get("location name"));
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                        bothLocationString = "pickeup";
                        LocationAddress.getAddressFromLocation(picDrpHash.get("location name"), getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                    }
                } else if (bothLocationString.equals("drop")) {
                    edt_drop_location.setText(picDrpHash.get("location name"));
                    if (Common.isNetworkAvailable(HomeActivity.this)) {
                        Log.d("Location name", "Location name = " + edt_pickup_location.getText().toString());
                        bothLocationString = "drop";
                        LocationAddress.getAddressFromLocation(picDrpHash.get("location name"), getApplicationContext(), new GeocoderHandlerLatitude());
                    } else {
                        Toast.makeText(HomeActivity.this, "No Network", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        layout_pickup_drag_location.setVisibility(View.GONE);
        //recycle_pickup_location.setVisibility(View.GONE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_home = null;
        layout_slidemenu = null;
        edt_pickup_location = null;
        edt_drop_location = null;
        edt_write_comment = null;

        layout_reservation = null;
        gpsTracker = null;
        googleMap = null;

        marker = null;
        PickupLarLng = null;
        DropLarLng = null;
        arrayPoints = null;
        NowDialog = null;
        ReservationDialog = null;
        cabDetailAdapter = null;
        txt_car_header = null;
        txt_car_descriptin = null;
        txt_first_price = null;
        txt_first_km = null;
        txt_sec_pric = null;
        txt_sec_km = null;
        txt_thd_price = null;
        layout_one = null;
        layout_two = null;
        layout_three = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("requestCode", "requestCode = " + requestCode);
        if (requestCode == 3) {
            if (data != null) {
                String userUpd = data.getStringExtra("update_user_profile").toString();
                Log.d("requestCode", "requestCode = " + userUpd);
                if (userUpd.equals("1")) {
                    common.SlideMenuDesign(slidingMenu, HomeActivity.this, "home");
                }
            }
        }
    }


    @Override
    public void onBackPressed() {

        if (slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.really_exit))
                    .setMessage(getResources().getString(R.string.are_you_sure))
                    .setNegativeButton(getResources().getString(R.string.cancel), null)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            HomeActivity.super.onBackPressed();
                            finish();
                        }
                    }).create().show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Common.mgr = new MapStateManager(this);
        Common.mgr.saveMapState(googleMap);
    }
}
