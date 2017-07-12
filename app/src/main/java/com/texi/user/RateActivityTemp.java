package com.texi.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.techintegrity.appu.R;
import com.texi.user.utils.Common;

public class RateActivityTemp extends AppCompatActivity {
    SlidingMenu slidingMenu;
    RelativeLayout layout_slidemenu;
    Common common = new Common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_temp);

        layout_slidemenu = (RelativeLayout)findViewById(R.id.layout_slidemenu);


        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu,RateActivityTemp.this,"rate card");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }
    @Override
    public void onBackPressed() {

        if(slidingMenu.isMenuShowing()){
            slidingMenu.toggle();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.really_exit))
                    .setMessage(getResources().getString(R.string.are_you_sure))
                    .setNegativeButton(getResources().getString(R.string.cancel), null)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {

//                            HomeActivity.super.onBackPressed();
                            finish();
                        }
                    }).create().show();
        }

    }


}
