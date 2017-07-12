package com.texi.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.techintegrity.appu.R;

public class FeedbackActivity extends AppCompatActivity {
  /*  RatingBar ratingBar;
    TextView rateMessage;
    String ratedValue;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
       /* ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        rateMessage = (TextView) findViewById(R.id.rateMessage);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this,R.color.yellow_texi), PorterDuff.Mode.SRC_ATOP);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratedValue = String.valueOf(ratingBar.getRating());
                rateMessage.setText("You have rated this App: "
                        + ratedValue + "/5.");
            }
        });*/
    }
}
