package com.application.tippzi.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.tippzi.Models.CategoryLIstModel;
import com.application.tippzi.Models.WalletModel;
import com.application.tippzi.R;

public class CategoryListAdapter extends BaseListAdapter  {

    Context context;
    public boolean select_flag ;

    protected OnEditCategoryListListener mOnEditCategoryButtonListener;

    public CategoryListAdapter(Context ctx) {
        super(ctx, R.layout.list_category);
        context = ctx;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CategoryLIstModel categoryListModel = (CategoryLIstModel) m_feedList.get(position);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_category, parent, false);
        }

        final ImageView category_background = ViewHolderHelper.get(convertView, R.id.iv_item_category);
        ImageView icon_location = ViewHolderHelper.get(convertView, R.id.iv_location_mark);
        TextView category_name = ViewHolderHelper.get(convertView, R.id.tv_category_name);

        if (categoryListModel.category.equals("Nightlife")) {
            category_background.setBackgroundResource(R.mipmap.ico_nightlife);
            icon_location.setBackgroundResource(R.mipmap.ico_nightlife_location);
            category_name.setText("Nightlife");
        } else if (categoryListModel.category.equals("Health & Fitness")) {
            category_background.setBackgroundResource(R.mipmap.ico_health_fitness);
            icon_location.setBackgroundResource(R.mipmap.ico_health_location);
            category_name.setText("Health & Fitness");
        } else if (categoryListModel.category.equals("Hair & Beauty")) {
            category_background.setBackgroundResource(R.mipmap.ico_hair_beauty);
            icon_location.setBackgroundResource(R.mipmap.ico_hair_location);
            category_name.setText("Hair & Beauty");
        }

        category_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnEditCategoryButtonListener.onClick(position);
            }
        });

        return convertView;
    }


    public void setOnClickButtonListener(OnEditCategoryListListener onEditCategoryListListener) {
        mOnEditCategoryButtonListener = onEditCategoryListListener;
    }

    public interface OnEditCategoryListListener {
        void onClick(int pos);
    }


}