package com.application.tippzi.Activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.OpenHourModel;
import com.application.tippzi.R;
import com.wefika.horizontalpicker.HorizontalPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditOpenHourScreen extends AbstractActivity implements HorizontalPicker.OnItemSelected, HorizontalPicker.OnItemClicked {

    private LinearLayout lay_opentime_1, lay_opentime_2, lay_close ;
    private TextView btn_switch_open_hours, time_open_start, time_open_end;
    private HorizontalPicker picker ;
    private boolean close_flag = true ;

    private int hour, sel_minute ;
    private SimpleDateFormat dateFormatter ;
    private String time ;
    private String select_open ;
    private Boolean check_open_time = false ;

    private CheckBox copy_paste;

    private int index_slide = 0;

    private OpenHourModel temp_model = new OpenHourModel();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile_open_hour);

        TextView bar_name = findViewById(R.id.tv_business_name_open_hour);
        bar_name.setText(GD.temp_bar.business_name);

        ImageView back = findViewById(R.id.iv_back_edit_open_hour);
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

        ImageView back_up = findViewById(R.id.iv_back_up_edit_open_hour);
        back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditDescriptionScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
                finish();
            }
        });

        TextView btn_continue = findViewById(R.id.btn_continue_edit_open_hour);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_slide == 0) {
                    if (!GD.temp_bar.open_time.mon_start.equals("")) {
                        check_open_time = true ;
                    }  else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.mon_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 1) {
                    if (!GD.temp_bar.open_time.tue_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.tue_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 2) {
                    if (!GD.temp_bar.open_time.wed_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.wed_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 3) {
                    if (!GD.temp_bar.open_time.thur_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.thur_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 4) {
                    if (!GD.temp_bar.open_time.fri_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.fri_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 5) {
                    if (!GD.temp_bar.open_time.sat_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.sat_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                } else if (index_slide == 6) {
                    if (!GD.temp_bar.open_time.sun_start.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                    if (!GD.temp_bar.open_time.sun_end.equals("")) {
                        check_open_time = true ;
                    }else {
                        check_open_time = false ;
                    }
                }
                if (check_open_time == true) {
                    Intent intent = new Intent(getApplicationContext(), EditGalleryScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Choose Opening Hours", Toast.LENGTH_LONG).show();
                }
            }
        });

        temp_model.mon_start = GD.temp_bar.open_time.mon_start;
        temp_model.mon_end = GD.temp_bar.open_time.mon_end;

        temp_model.tue_start = GD.temp_bar.open_time.tue_start;
        temp_model.tue_end = GD.temp_bar.open_time.tue_end;

        temp_model.thur_start = GD.temp_bar.open_time.thur_start;
        temp_model.thur_end = GD.temp_bar.open_time.thur_end;

        temp_model.wed_start = GD.temp_bar.open_time.wed_start;
        temp_model.wed_end = GD.temp_bar.open_time.wed_end;

        temp_model.fri_start = GD.temp_bar.open_time.fri_start;
        temp_model.fri_end = GD.temp_bar.open_time.fri_end;

        temp_model.sat_start = GD.temp_bar.open_time.sat_start;
        temp_model.sat_end = GD.temp_bar.open_time.sat_end;

        temp_model.sun_start = GD.temp_bar.open_time.sun_start;
        temp_model.sun_end = GD.temp_bar.open_time.sun_end;

        copy_paste = findViewById(R.id.ch_copy_paste);
        copy_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (copy_paste.isChecked()) {
                    if (close_flag == true) {
                        String start_open = time_open_start.getText().toString();
                        String end_open = time_open_end.getText().toString();

                        GD.temp_bar.open_time.close_flag_mon = true;
                        GD.temp_bar.open_time.mon_start = start_open;
                        GD.temp_bar.open_time.mon_end = end_open;

                        GD.temp_bar.open_time.close_flag_tue = true;
                        GD.temp_bar.open_time.tue_start = start_open;
                        GD.temp_bar.open_time.tue_end = end_open;

                        GD.temp_bar.open_time.close_flag_thur = true;
                        GD.temp_bar.open_time.thur_start = start_open;
                        GD.temp_bar.open_time.thur_end = end_open;

                        GD.temp_bar.open_time.close_flag_wed = true;
                        GD.temp_bar.open_time.wed_start = start_open;
                        GD.temp_bar.open_time.wed_end = end_open;

                        GD.temp_bar.open_time.close_flag_fri = true;
                        GD.temp_bar.open_time.fri_start = start_open;
                        GD.temp_bar.open_time.fri_end = end_open;

                        GD.temp_bar.open_time.close_flag_sat = true;
                        GD.temp_bar.open_time.sat_start = start_open;
                        GD.temp_bar.open_time.sat_end = end_open;

                        GD.temp_bar.open_time.close_flag_sun = true;
                        GD.temp_bar.open_time.sun_start = start_open;
                        GD.temp_bar.open_time.sun_end = end_open;
                    } else {
                        GD.temp_bar.open_time.close_flag_mon = false;
                        GD.temp_bar.open_time.mon_start = "";
                        GD.temp_bar.open_time.mon_end = "";

                        GD.temp_bar.open_time.close_flag_tue = false;
                        GD.temp_bar.open_time.tue_start = "";
                        GD.temp_bar.open_time.tue_end = "";

                        GD.temp_bar.open_time.close_flag_thur = false;
                        GD.temp_bar.open_time.thur_start = "";
                        GD.temp_bar.open_time.thur_end = "";

                        GD.temp_bar.open_time.close_flag_wed = false;
                        GD.temp_bar.open_time.wed_start = "";
                        GD.temp_bar.open_time.wed_end = "";

                        GD.temp_bar.open_time.close_flag_fri = false;
                        GD.temp_bar.open_time.fri_start = "";
                        GD.temp_bar.open_time.fri_end = "";

                        GD.temp_bar.open_time.close_flag_sat = false;
                        GD.temp_bar.open_time.sat_start = "";
                        GD.temp_bar.open_time.sat_end = "";

                        GD.temp_bar.open_time.close_flag_sun = false;
                        GD.temp_bar.open_time.sun_start = "";
                        GD.temp_bar.open_time.sun_end = "";
                    }
                } else {
                    GD.temp_bar.open_time.mon_start = temp_model.mon_start;
                    GD.temp_bar.open_time.mon_end = temp_model.mon_end;

                    GD.temp_bar.open_time.tue_start = temp_model.tue_start;
                    GD.temp_bar.open_time.tue_end = temp_model.tue_end;

                    GD.temp_bar.open_time.thur_start = temp_model.thur_start;
                    GD.temp_bar.open_time.thur_end = temp_model.thur_end;

                    GD.temp_bar.open_time.wed_start = temp_model.wed_start;
                    GD.temp_bar.open_time.wed_end = temp_model.wed_end;

                    GD.temp_bar.open_time.fri_start = temp_model.fri_start;
                    GD.temp_bar.open_time.fri_end = temp_model.fri_end;

                    GD.temp_bar.open_time.sat_start = temp_model.sat_start;
                    GD.temp_bar.open_time.sat_end = temp_model.sat_end;

                    GD.temp_bar.open_time.sun_start = temp_model.sun_start;
                    GD.temp_bar.open_time.sun_end = temp_model.sun_end;

                    GD.temp_bar.open_time.close_flag_mon = true;
                    GD.temp_bar.open_time.close_flag_tue = true;
                    GD.temp_bar.open_time.close_flag_thur = true;
                    GD.temp_bar.open_time.close_flag_wed = true;
                    GD.temp_bar.open_time.close_flag_fri = true;
                    GD.temp_bar.open_time.close_flag_sat = true;
                    GD.temp_bar.open_time.close_flag_sun = true;
                }
            }
        });

        time_open_start = findViewById(R.id.tv_open_start_time);
        time_open_end = findViewById(R.id.tv_open_end_time);

        lay_opentime_1 = findViewById(R.id.lay_opentime1);
        lay_opentime_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_open = "open1";
                OpenTime();
            }
        });

        lay_opentime_2 = findViewById(R.id.lay_opentime2);
        lay_opentime_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_open = "open2";
                OpenTime();
            }
        });

        lay_close = findViewById(R.id.lay_close);

        btn_switch_open_hours = findViewById(R.id.tv_switch_open_hours);
        btn_switch_open_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (close_flag == true) {
                    lay_opentime_1.setVisibility(View.GONE);
                    lay_opentime_2.setVisibility(View.GONE);
                    lay_close.setVisibility(View.VISIBLE);
                    btn_switch_open_hours.setText("Add opening hours");
                    close_flag = false ;
                    if (index_slide == 0) {
                        GD.temp_bar.open_time.mon_start = "";
                        GD.temp_bar.open_time.mon_end = "";
                    } else if (index_slide == 1) {
                        GD.temp_bar.open_time.tue_start = "";
                        GD.temp_bar.open_time.tue_end = "";
                    } else if (index_slide == 2) {
                        GD.temp_bar.open_time.wed_start = "";
                        GD.temp_bar.open_time.wed_end = "";
                    } else if (index_slide == 3) {
                        GD.temp_bar.open_time.thur_start = "";
                        GD.temp_bar.open_time.thur_end = "";
                    } else if (index_slide == 4) {
                        GD.temp_bar.open_time.fri_start = "";
                        GD.temp_bar.open_time.fri_end = "";
                    } else if (index_slide == 5) {
                        GD.temp_bar.open_time.sat_start = "";
                        GD.temp_bar.open_time.sat_end = "";
                    } else if (index_slide == 6) {
                        GD.temp_bar.open_time.sun_start = "";
                        GD.temp_bar.open_time.sun_end = "";
                    }
                } else {
                    lay_opentime_1.setVisibility(View.VISIBLE);
                    lay_opentime_2.setVisibility(View.VISIBLE);
                    lay_close.setVisibility(View.GONE);
                    btn_switch_open_hours.setText("We're closed today");
                    close_flag = true ;
                    if (index_slide == 0) {
                        if (GD.temp_bar.open_time.mon_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.mon_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.mon_start);
                        }

                        if (GD.temp_bar.open_time.mon_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.mon_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.mon_end);
                        }
                    } else if (index_slide == 1) {
                        if (GD.temp_bar.open_time.tue_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.tue_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.tue_start);
                        }

                        if (GD.temp_bar.open_time.tue_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.tue_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.tue_end);
                        }
                    } else if (index_slide == 2) {
                        if (GD.temp_bar.open_time.wed_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.wed_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.wed_start);
                        }

                        if (GD.temp_bar.open_time.wed_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.wed_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.wed_end);
                        }
                    } else if (index_slide == 3) {
                        if (GD.temp_bar.open_time.thur_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.thur_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.thur_start);
                        }

                        if (GD.temp_bar.open_time.thur_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.thur_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.thur_end);
                        }
                    } else if (index_slide == 4) {
                        if (GD.temp_bar.open_time.fri_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.fri_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.fri_start);
                        }

                        if (GD.temp_bar.open_time.fri_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.fri_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.fri_end);
                        }
                    } else if (index_slide == 5) {
                        if (GD.temp_bar.open_time.sat_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.sat_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.sat_start);
                        }

                        if (GD.temp_bar.open_time.sat_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.sat_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.sat_end);
                        }
                    } else if (index_slide == 6) {
                        if (GD.temp_bar.open_time.sun_start.equals("")) {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_start.setText(GD.temp_bar.open_time.sun_start);
                        } else {
                            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_start.setText(GD.temp_bar.open_time.sun_start);
                        }

                        if (GD.temp_bar.open_time.sun_end.equals("")) {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                            time_open_end.setText(GD.temp_bar.open_time.sun_end);
                        } else {
                            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                            time_open_end.setText(GD.temp_bar.open_time.sun_end);
                        }
                    }
                }
            }
        });

        picker = findViewById(R.id.picker);
        picker.setOnItemClickedListener(this);
        picker.setOnItemSelectedListener(this);

        lay_opentime_1.setVisibility(View.VISIBLE);
        lay_opentime_2.setVisibility(View.VISIBLE);
        lay_close.setVisibility(View.GONE);
        btn_switch_open_hours.setText("We're closed today");
        if (GD.temp_bar.open_time.mon_start.equals("")) {
            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
            time_open_start.setText(GD.temp_bar.open_time.mon_start);
        } else {
            lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
            time_open_start.setText(GD.temp_bar.open_time.mon_start);
        }

        if (GD.temp_bar.open_time.mon_end.equals("")) {
            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
            time_open_end.setText(GD.temp_bar.open_time.mon_end);
        } else {
            lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
            time_open_end.setText(GD.temp_bar.open_time.mon_end);
        }
    }

    @Override
    public void onItemSelected(int index) {
        index_slide = index;
        copy_paste.setChecked(false);
        refresh_open_hour();
    }

    private void refresh_open_hour() {
        if (index_slide == 0) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.mon_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.mon_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.mon_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.mon_end.equals("")) {
                check_open_time = false ;
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.mon_end);
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.mon_end);
                check_open_time = true ;
            }
        } else if (index_slide == 1) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.tue_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.tue_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.tue_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.tue_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.tue_end);
                check_open_time = false ;

            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.tue_end);
                check_open_time = true ;
            }
        } else if (index_slide == 2) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.wed_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.wed_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.wed_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.wed_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.wed_end);
                check_open_time = false ;
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.wed_end);
                check_open_time = true ;
            }
        } else if (index_slide == 3) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.thur_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.thur_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.thur_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.thur_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.thur_end);
                check_open_time = false ;
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.thur_end);
                check_open_time = true ;
            }
        } else if (index_slide == 4) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.fri_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.fri_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.fri_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.fri_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.fri_end);
                check_open_time = false ;
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.fri_end);
                check_open_time = true ;
            }
        } else if (index_slide == 5) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.sat_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.sat_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.sat_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.sat_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.sat_end);
                check_open_time = false ;
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.sat_end);
                check_open_time = true ;
            }
        } else if (index_slide == 6) {
            lay_opentime_1.setVisibility(View.VISIBLE);
            lay_opentime_2.setVisibility(View.VISIBLE);
            lay_close.setVisibility(View.GONE);
            btn_switch_open_hours.setText("We're closed today");
            if (GD.temp_bar.open_time.sun_start.equals("")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_start.setText(GD.temp_bar.open_time.sun_start);
                check_open_time = false ;
            } else {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(GD.temp_bar.open_time.sun_start);
                check_open_time = true ;
            }

            if (GD.temp_bar.open_time.sun_end.equals("")) {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_inactive);
                time_open_end.setText(GD.temp_bar.open_time.sun_end);
                check_open_time = false ;
            } else {
                lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_end.setText(GD.temp_bar.open_time.sun_end);
                check_open_time = true ;
            }
        }
    }

    @Override
    public void onItemClicked(int index) {

    }

    private void OpenTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String current = sdf.format(new Date());
        int current_hour = 0;
        int current_minute = 0;
        if (hour  > 0 ) {
            current_hour = hour ;
            current_minute = sel_minute ;
        } else {
            current_hour = Integer.valueOf(current.substring(11, 13));
            current_minute = Integer.valueOf(current.substring(14));
        }


        new TimePickerDialog(this,timelistener, current_hour, current_minute, true).show();
    }

    private TimePickerDialog.OnTimeSetListener timelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            sel_minute = minute;
            time = pad(hour) + ":" + pad(sel_minute) ;
            final Date a, b;
            if (select_open.equals("open1")) {
                lay_opentime_1.setBackgroundResource(R.drawable.rect_edittext_active);
                time_open_start.setText(time);
                if (index_slide == 0) {
                    GD.temp_bar.open_time.mon_start = time;
                } else if (index_slide == 1) {
                    GD.temp_bar.open_time.tue_start = time;
                } else if (index_slide == 2) {
                    GD.temp_bar.open_time.wed_start = time;
                } else if (index_slide == 3) {
                    GD.temp_bar.open_time.thur_start = time;
                } else if (index_slide == 4) {
                    GD.temp_bar.open_time.fri_start = time;
                } else if (index_slide == 5) {
                    GD.temp_bar.open_time.sat_start = time;
                } else if (index_slide == 6) {
                    GD.temp_bar.open_time.sun_start = time;
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    a = sdf.parse(time_open_start.getText().toString());
                    b = sdf.parse(time);

                    lay_opentime_2.setBackgroundResource(R.drawable.rect_edittext_active);
                    time_open_end.setText(time);
                    if (index_slide == 0) {
                        GD.temp_bar.open_time.mon_end = time;
                    } else if (index_slide == 1) {
                        GD.temp_bar.open_time.tue_end = time;
                    } else if (index_slide == 2) {
                        GD.temp_bar.open_time.wed_end = time;
                    } else if (index_slide == 3) {
                        GD.temp_bar.open_time.thur_end = time;
                    } else if (index_slide == 4) {
                        GD.temp_bar.open_time.fri_end = time;
                    } else if (index_slide == 5) {
                        GD.temp_bar.open_time.sat_end = time;
                    } else if (index_slide == 6) {
                        GD.temp_bar.open_time.sun_end = time;
                    }

//                    if(a.compareTo(b) > 0){
//                        Toast.makeText(getApplicationContext(), "Out time should be greater than In time", Toast.LENGTH_LONG).show();
//                    } else {
//
//                    }
                } catch (ParseException ex) {

                }
            }
        }
    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditDescriptionScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.back_left_out, R.anim.back_right_in);
        finish();
    }
}
