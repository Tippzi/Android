package com.application.tippzi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.tippzi.Adapters.OnSwipeTouchListener;
import com.application.tippzi.R;

public class Tutorial4Screen extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toturial4);

        TextView skip = findViewById(R.id.btn_skip_tutorial4);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        });

        RelativeLayout background = findViewById(R.id.activity_tutorial4);
        background.setOnTouchListener(new OnSwipeTouchListener(Tutorial4Screen.this){
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                Intent intent = new Intent(getApplicationContext(), Tutorial3Screen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();

            }
            public void onSwipeLeft() {
                Intent intent = new Intent(getApplicationContext(), CustomerDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
            public void onSwipeBottom() {

            }
        });

    }
}
