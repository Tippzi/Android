package com.application.tippzi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.tippzi.Global.GD;
import com.application.tippzi.R;

import static com.application.tippzi.Global.CF.EMOJI_FILTER;

public class EditDescriptionScreen extends AbstractActivity {

    private EditText business_description, business_music_type;
    private TextView comment_music_type, comment_comma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile_description);

        ImageView back = findViewById(R.id.iv_back_edit_description);
        ImageView back_up = findViewById(R.id.iv_back_up_edit_description);

        comment_music_type = (TextView) findViewById(R.id.tv_comment_music_edit);
        comment_comma = (TextView) findViewById(R.id.tv_comment_comma);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusinessDashboardScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditAccountProfileScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        TextView bar_name = findViewById(R.id.tv_business_name_description);
        bar_name.setText(GD.temp_bar.business_name);

        business_description = findViewById(R.id.ed_business_description_edit);
        business_music_type = findViewById(R.id.ed_business_music_edit);

        business_description.setFilters(new InputFilter[]{EMOJI_FILTER});
        business_music_type.setFilters(new InputFilter[]{EMOJI_FILTER});

        business_description.setText(GD.temp_bar.description);
        business_music_type.setText(GD.temp_bar.music_type);

        business_description.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(500)
        });

        TextView btn_continue = findViewById(R.id.btn_continue_edit_account);

        if (!GD.temp_bar.category.equals("Nightlife")) {
            comment_music_type.setVisibility(View.GONE);
            business_music_type.setVisibility(View.GONE);
            comment_comma.setVisibility(View.GONE);
        } else {
            comment_music_type.setVisibility(View.VISIBLE);
            business_music_type.setVisibility(View.VISIBLE);
            comment_comma.setVisibility(View.VISIBLE);
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GD.temp_bar.description = business_description.getText().toString();
                GD.temp_bar.music_type = business_music_type.getText().toString();

                Intent intent = new Intent(getApplicationContext(), EditOpenHourScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditAccountProfileScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}
