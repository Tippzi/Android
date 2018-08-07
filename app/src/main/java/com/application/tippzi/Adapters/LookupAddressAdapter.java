package com.application.tippzi.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.tippzi.Models.AddressModel;
import com.application.tippzi.R;


/**
 * Created by E on 10/18/2017.
 */

public class LookupAddressAdapter extends BaseListAdapter  {

    Context context;
    public boolean select_flag ;

    protected OnClickButtonSetListListener mOnClickButtonListener;

    public LookupAddressAdapter(Context ctx) {
        super(ctx, R.layout.list_address_item);
        context = ctx;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final AddressModel addressModel = (AddressModel) m_feedList.get(position);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_address_item, parent, false);
        }

        final TextView address = ViewHolderHelper.get(convertView, R.id.tv_address);
        address.setText(addressModel.address);
        if (addressModel.select_flag){
            address.setBackgroundColor(context.getResources().getColor(R.color.SickHint));
        } else {
            address.setBackgroundColor(Color.TRANSPARENT);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnClickButtonListener.onClick(position, addressModel.address);
            }
        });
        return convertView;
    }


    public void setOnClickButtonListener(OnClickButtonSetListListener onClickButtonListener)
    {
        mOnClickButtonListener = onClickButtonListener;
    }

    public interface OnClickButtonSetListListener {

        void onClick(int pos, String select_address);
    }


}