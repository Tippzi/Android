package com.application.tippzi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarDetailModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by E on 11/5/2017.
 */

public class DetailBarAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DealModel> barDetailModelArrayList;
    protected OnClickButtonSetListListener mOnClickButtonListener;

    public DetailBarAdapter(Context context, ArrayList<DealModel> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setData(viewHolder, (DealModel) getItem(position));
        return convertView;
    }

    private void setData(ViewHolder viewHolder, final DealModel currentItem) {
        if (currentItem.deal_title.length() > 25) {
            viewHolder.title.setText(currentItem.deal_title.substring(0, 25) + "...");
        } else {
            viewHolder.title.setText(currentItem.deal_title);
        }
        viewHolder.deal_description_slide.setText(currentItem.deal_description);
        viewHolder.deal_remain_slide.setText("Hurry, only " + String.valueOf(currentItem.deal_qty - currentItem.claimed) + " left!");
        viewHolder.deal_duration_slide.setText("Exp " + currentItem.deal_duration);

        if (GD.select_user_type.equals("business")) {
            viewHolder.claim_deal.setVisibility(View.GONE);
            viewHolder.ticket_button.setVisibility(View.GONE);
        }
        if (currentItem.claim_check == true) {
            viewHolder.ticket.setBackgroundResource(R.mipmap.ico_hint_ticket);
            viewHolder.claimed_show.setVisibility(View.VISIBLE);
            viewHolder.ticket_button.setBackgroundResource(R.mipmap.ico_hint_ticket_button);
            viewHolder.claim_deal.setBackgroundResource(R.drawable.rect_hint_button);
        }
//        viewHolder.claim_deal.setText("    Click to claim this deal");
        viewHolder.claim_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickButtonListener.onClick(currentItem.index_number);
            }
        });
    }

    public void setOnClickButtonListener(OnClickButtonSetListListener onClickButtonListener)
    {
        mOnClickButtonListener = onClickButtonListener;
    }

    public interface OnClickButtonSetListListener {

        void onClick(int pos);
    }
    static class ViewHolder {
        @BindView(R.id.tv_deal_title_slide)
        TextView title;
        @BindView(R.id.tv_deal_description_slide)
        TextView deal_description_slide;
        @BindView(R.id.tv_deal_remain_slide)
        TextView deal_remain_slide;
        @BindView(R.id.tv_deal_duration_slide)
        TextView deal_duration_slide;
        @BindView(R.id.btn_claim_deal)
        TextView claim_deal;
        @BindView(R.id.iv_ticket_button)
        ImageView ticket_button;
        @BindView(R.id.iv_ticket)
        ImageView ticket;
        @BindView(R.id.tv_claimed_show)
        TextView claimed_show;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
