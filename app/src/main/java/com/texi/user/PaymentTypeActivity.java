package com.texi.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.texi.user.utils.AllTripFeed;
import com.texi.user.utils.Common;
import com.texi.user.utils.Url;
import com.texi.user.utils.UserApplication;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import com.techintegrity.appu.R;

public class PaymentTypeActivity extends AppCompatActivity {
    RelativeLayout back_arrow;
    RelativeLayout cash;
    RelativeLayout creditCard;
    RelativeLayout paytm;
    View.OnClickListener clickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_cash);

        cash = (RelativeLayout) findViewById(R.id.layout_cash);
        paytm = (RelativeLayout) findViewById(R.id.layout_cash_paytm);
        creditCard = (RelativeLayout) findViewById(R.id.layout_cash_credit_card);


        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT);
                Intent trackCarIntent = new Intent(PaymentTypeActivity.this, TrackingActivity.class);
                trackCarIntent.putExtra("paymentMade", true);
                trackCarIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(trackCarIntent);
                finish();

            }
        };

        cash.setOnClickListener(clickListener);
        creditCard.setOnClickListener(clickListener);
        paytm.setOnClickListener(clickListener);


        back_arrow = (RelativeLayout) findViewById(R.id.layout_back_arrow);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PaymentTypeActivity.this)
                        .setTitle(getResources().getString(R.string.really_exit))
                        .setMessage(getResources().getString(R.string.alert_cancel))
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                PaymentTypeActivity.super.onBackPressed();
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(PaymentTypeActivity.this)
                .setTitle(getResources().getString(R.string.really_exit))
                .setMessage(getResources().getString(R.string.alert_cancel))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        PaymentTypeActivity.super.onBackPressed();
                        finish();
                    }
                }).create().show();


    }
}
