package com.texi.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.techintegrity.appu.R;
import com.texi.user.utils.Common;

public class PromoCode extends AppCompatActivity {
    SlidingMenu slidingMenu;
    RelativeLayout layout_slidemenu;
    Common common = new Common();
    LinearLayout edittextContainer;
    TextView appliedText;
    EditText codetext;
    RelativeLayout layout_submit;
    ProgressDialog progressBar;
    private Typeface regularRoboto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);

        final RelativeLayout rlMainView;
        final TextView tvTitle;
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");



            //Error Alert
            rlMainView=(RelativeLayout)findViewById(R.id.rlMainView);
            tvTitle=(TextView)findViewById(R.id.tvTitle);

        layout_slidemenu = (RelativeLayout) findViewById(R.id.layout_menu);
        edittextContainer = (LinearLayout) findViewById(R.id.edit_text_container);
        appliedText = (TextView) findViewById(R.id.text_applied);
        codetext = (EditText) findViewById(R.id.edit_code);
        layout_submit = (RelativeLayout) findViewById(R.id.layout_submit);
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setTitle("Checking");
        progressBar.setMessage("Please Wait....");


        appliedText.setVisibility(View.GONE);


        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        common.SlideMenuDesign(slidingMenu, PromoCode.this, "promo code");

        layout_slidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });


        layout_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codetext.getText().toString().trim().equals("")) {
                   Common.showMkError(PromoCode.this,"23");
                } else {
                    progressBar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.dismiss();
                            if (codetext.getText().toString().trim().equals("abc")) {
                                edittextContainer.setVisibility(View.GONE);
                                appliedText.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(PromoCode.this, "Invalid Promo Code", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 2000);

                }
            }
        });
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

//                            HomeActivity.super.onBackPressed();
                            finish();
                        }
                    }).create().show();
        }

    }
}
