package com.application.tippzi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by E on 1/5/2018.
 */

public class BusinessDetailBarAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DealModel> barDetailModelArrayList;

    public BusinessDetailBarAdapter(Context context, ArrayList<DealModel> items) {
        this.context = context;
        this.barDetailModelArrayList = items;
    }

    @Override
    public int getCount() {
        return barDetailModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return barDetailModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.map_card, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setData(viewHolder, (DealModel) getItem(position));
        return convertView;
    }

    private void setData(ViewHolder viewHolder, final DealModel currentItem) {
        if (currentItem.deal_title.length() > 19) {
            viewHolder.title.setText(currentItem.deal_title.substring(0, 19) + "...");
        } else {
            viewHolder.title.setText(currentItem.deal_title);
        }
        if (currentItem.deal_description.length() > 60) {
            viewHolder.deal_description_slide.setText(currentItem.deal_description.substring(0, 60) + "...");
        } else {
            viewHolder.deal_description_slide.setText(currentItem.deal_description);
        }

//        if (GD.category_option.equals("")) {
//            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_ticket_active);
//        } else if (GD.category_option.equals("Nightlife")) {
//            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_ticket_active);
//        } else if (GD.category_option.equals("Health & Fitness")) {
//            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_yellow_ticket);
//        } else if (GD.category_option.equals("Hair & Beauty")) {
//            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_red_ticket);
//        }
        String category = currentItem.category;
        if(category.equals("Nightlife")){
            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_ticket_active);
        } else if(category.equals("Health & Fitness")){
            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_yellow_ticket);
        } else if(category.equals("Hair & Beauty")){
            viewHolder.ticket_google_map.setBackgroundResource(R.mipmap.ico_red_ticket);
        }

    }

    static class ViewHolder {
        @BindView(R.id.tv_deal_title_google_map)
        TextView title;
        @BindView(R.id.tv_deal_description_google_map)
        TextView deal_description_slide;
        @BindView(R.id.iv_ticket_google_map)
        ImageView ticket_google_map;
        @BindView(R.id.lay_deal_card)
        RelativeLayout deal_card;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }



}