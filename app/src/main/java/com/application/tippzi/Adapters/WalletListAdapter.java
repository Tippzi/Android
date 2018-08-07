package com.application.tippzi.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.R;

public class WalletListAdapter extends BaseListAdapter  {

    Context context;
    public boolean select_flag ;

    protected OnEditDealListListener mOnEditDealButtonListener, mOnRemoveDealListener;

    public WalletListAdapter(Context ctx) {
        super(ctx, R.layout.list_customer_wallet);
        context = ctx;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final WalletModel walletListModel = (WalletModel) m_feedList.get(position);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_customer_wallet, parent, false);
        }

        final TextView deal_title = ViewHolderHelper.get(convertView, R.id.tv_deal_title);
        final TextView deal_description = ViewHolderHelper.get(convertView, R.id.tv_deal_description);
        final TextView bar_name = ViewHolderHelper.get(convertView, R.id.tv_bar_name);
        final LinearLayout btn_edit_deal = ViewHolderHelper.get(convertView, R.id.lay_wallet_list_item);
        final ImageView location_marker = ViewHolderHelper.get(convertView, R.id.iv_locate);
        final TextView remain = ViewHolderHelper.get(convertView, R.id.tv_remain);
        final ImageView btn_remove = ViewHolderHelper.get(convertView, R.id.iv_remove_wallet);

        if (walletListModel.deal_title.length() > 20) {
            deal_title.setText(walletListModel.deal_title.substring(0, 20) + "...");
        } else {
            deal_title.setText(walletListModel.deal_title);
        }

        if (walletListModel.deal_description.length() > 40) {
            deal_description.setText(walletListModel.deal_description.substring(0, 30) + "...");
        } else {
            deal_description.setText(walletListModel.deal_description);
        }

        if (walletListModel.bar_name.length() > 15) {
            bar_name.setText(walletListModel.bar_name.substring(0, 14) + "...");
        } else {
            bar_name.setText(walletListModel.bar_name);
        }

        if (walletListModel.category.equals("Nightlife")) {
            location_marker.setBackgroundResource(R.mipmap.ico_nightlife_location);
            remain.setTextColor(Color.parseColor("#64318E"));
        } else if (walletListModel.category.equals("Health & Fitness")) {
            location_marker.setBackgroundResource(R.mipmap.ico_health_location);
            remain.setTextColor(Color.parseColor("#A0C23C"));
        } else if (walletListModel.category.equals("Hair & Beauty")) {
            location_marker.setBackgroundResource(R.mipmap.ico_hair_location);
            remain.setTextColor(Color.parseColor("#932974"));
        }

        remain.setText(String.valueOf(walletListModel.deal_qty - walletListModel.claimed) + " left   " + "Exp " + walletListModel.deal_duration);

        btn_edit_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEditDealButtonListener.onClick(position);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnRemoveDealListener.onClick(position);
            }
        });

        return convertView;
    }


    public void setOnClickButtonListener(OnEditDealListListener oneditdealListener) {
        mOnEditDealButtonListener = oneditdealListener;
    }

    public void setOnRemoveDealListener(OnEditDealListListener onRemoveDealListener) {
        mOnRemoveDealListener = onRemoveDealListener;
    }

    public interface OnEditDealListListener {
        void onClick(int pos);
    }


}