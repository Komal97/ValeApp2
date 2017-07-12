package com.texi.user;


import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.techintegrity.appu.R;
import com.texi.user.clock.AnalogChronometer;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;
import com.tomerrosenfeld.customanalogclockview.DialOverlay;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogTimerFragment extends DialogFragment {
    ImageButton cancel_button;
    View view;
//    CountDownTimer timer;
    private static final String FORMAT = "%02d:%02d";
//    TextView tv;
//    private final long MAX_TIME = 900000;
//    private long value_15=15;
//    private AnalogChronometer chronometer;

    public DialogTimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view =  inflater.inflate(R.layout.fragment_dialog_timer, container, false);
//       tv=(TextView) view.findViewById(R.id.timer_text);
        getDialog().getWindow().setLayout(250,400);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


//        getDialog().setTitle(R.string.timer);
        return view;
    }




    @Override
    public void onStart() {
        super.onStart();

//      chronometer = (AnalogChronometer) view.findViewById(R.id.chronometer);
//
//        chronometer.setBase(SystemClock.elapsedRealtime()- MAX_TIME);
//
//        chronometer.setOnChronometerTickListener(new AnalogChronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(AnalogChronometer chronometer) {
//                String time_string = String.format(FORMAT, chronometer.getTime().first, chronometer.getTime().second);
//                tv.setText(time_string);
//            }
//        });
//
//        chronometer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
//        chronometer.stop();
//        chronometer=null;

    }
}
