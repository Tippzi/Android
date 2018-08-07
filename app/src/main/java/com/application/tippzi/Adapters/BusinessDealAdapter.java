package com.application.tippzi.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.tippzi.Models.AddressModel;
import com.application.tippzi.Models.DealDaysModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.R;


/**
 * Created by E on 10/18/2017.
 */

public class BusinessDealAdapter extends BaseListAdapter  {

    protected OnClickButtonEditDealListListener meditdealListener, mRemoveDealListner;

    public BusinessDealAdapter(Context ctx) {
        super(ctx, R.layout.list_business_deal_item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DealModel dealModel = (DealModel) m_feedList.get(position);

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_business_deal_item,parent,false);
        }

        TextView deal_title = convertView.findViewById(R.id.tv_deal_title);
        TextView deal_description = convertView.findViewById(R.id.tv_deal_description);
        TextView valid = convertView.findViewById(R.id.tv_valid);
        TextView wink = convertView.findViewById(R.id.tv_wink);
        TextView redeem = convertView.findViewById(R.id.tv_redeem);
        TextView wallet = convertView.findViewById(R.id.tv_wallet);
        TextView editdeal = convertView.findViewById(R.id.btn_edit_deal);
        ImageView remove_deal = convertView.findViewById(R.id.iv_remove_deal);
        convertView.setBackgroundColor(Color.TRANSPARENT);

        if (dealModel.deal_title.length() > 25) {
            deal_title.setText(dealModel.deal_title.substring(0, 25) + "...");
        } else {
            deal_title.setText(dealModel.deal_title);
        }

        if (dealModel.deal_description.length() > 30) {
            deal_description.setText(dealModel.deal_description.substring(0, 30) + "...");
        } else {
            deal_description.setText(dealModel.deal_description);
        }

        String valid_text = "Valid ";
        if (dealModel.deal_days.monday == true) {
            valid_text = valid_text + "Mon, ";
        }

        if (dealModel.deal_days.tuesday == true) {
            valid_text = valid_text + "Tue, ";
        }

        if (dealModel.deal_days.wednesday == true) {
            valid_text = valid_text + "Wed, ";
        }

        if (dealModel.deal_days.thursday == true) {
            valid_text = valid_text + "Thur, ";
        }

        if (dealModel.deal_days.friday == true) {
            valid_text = valid_text + "Fri, ";
        }

        if (dealModel.deal_days.saturday == true) {
            valid_text = valid_text + "Sat, ";
        }

        if (dealModel.deal_days.sunday == true) {
            valid_text = valid_text + "Sun, ";
        }

        valid_text = valid_text + "Exp " + dealModel.deal_duration;

        valid.setText(valid_text);

        wink.setText(String.valueOf(dealModel.impressions));

        redeem.setText(String.valueOf(dealModel.claimed));

        wallet.setText(String.valueOf(dealModel.in_wallet));

        editdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meditdealListener.onClick(position);
            }
        });

        remove_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRemoveDealListner.onClick(position);
            }
        });
        return convertView;
    }


    public void setOnClickEditListener(OnClickButtonEditDealListListener onClickButtonListener)
    {
        meditdealListener = onClickButtonListener;
    }

    public void setRemoveDealListener(OnClickButtonEditDealListListener onRemoveDealListener)
    {
        mRemoveDealListner = onRemoveDealListener;
    }

    public interface OnClickButtonEditDealListListener {

        void onClick(int pos);
    }


}