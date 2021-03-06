package com.application.tippzi.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.tippzi.Global.GD;
import com.application.tippzi.R;
import com.google.android.gms.maps.GoogleMap;
import com.mapbox.mapboxsdk.annotations.Marker;

public class BarTitleViewAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;
    private TextView textViewTitle;

    public BarTitleViewAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        View v = inflater.inflate(R.layout.bar_title_location_default, null);
        if (marker != null) {
            textViewTitle = v.findViewById(R.id.tv_bar_name);
            textViewTitle.setText(marker.getTitle());
            LinearLayout layout = v.findViewById(R.id.lay_bar_title);
            if (GD.category_option.equals("")) {
                layout.setBackgroundResource(R.drawable.rect_map_title);
            } else if (GD.category_option.equals("Nightlife")) {
                layout.setBackgroundResource(R.drawable.rect_map_title);
            } else if (GD.category_option.equals("Health & Fitness")) {
                layout.setBackgroundResource(R.drawable.rect_map_title_yellow);
            } else if (GD.category_option.equals("Hair & Beauty")) {
                layout.setBackgroundResource(R.drawable.rect_map_title_red);
            }

        }
        v.setBackgroundColor(Color.TRANSPARENT);
        return (v);
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        return null;
    }
}
