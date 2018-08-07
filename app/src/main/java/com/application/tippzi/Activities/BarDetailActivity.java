package com.application.tippzi.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.tippzi.Adapters.DetailBarAdapter;
import com.application.tippzi.Global.GD;
import com.application.tippzi.Models.BarDetailModel;
import com.application.tippzi.Models.DealDaysModel;
import com.application.tippzi.Models.DealModel;
import com.application.tippzi.R;
import com.application.tippzi.swipeview.SwipeAdapterView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BarDetailActivity extends AbstractActivity implements SwipeAdapterView.onSwipeListener {

    private RollPagerView mViewPager;
    private ImageView bardetail_back ;
    private TextView count_page ;
    private ArrayList<String> bitmapArrayList = new ArrayList<>();
    //Swipe view
    private ArrayList<DealModel> dataList;
    private DetailBarAdapter dummyAdapter;
    private SwipeAdapterView cardsContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_details);

        GD.slide_index = 0 ;

        //swipe part
        ButterKnife.bind(this);

        TextView title_add_deal = findViewById(R.id.tv_title_business_details);
        TextView music_type = findViewById(R.id.tv_music_type_business_details);

        title_add_deal.setText(GD.businessModel.bars.get(0).business_name);
        music_type.setText(GD.businessModel.bars.get(0).music_type);

        //background image part
        mViewPager = findViewById(R.id.view_pager);
        count_page = findViewById(R.id.tv_count_page);
        bardetail_back = findViewById(R.id.iv_back_bar_details);
        cardsContainer = findViewById(R.id.container);

        getBarDeatilData();
        setAdapters();
        setListeners();

        bitmapArrayList = new ArrayList<>();
        if (!GD.businessModel.bars.get(0).galleryModel.background1.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background1);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background2.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background2);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background3.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background3);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background4.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background4);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background5.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background5);
        }

        if (!GD.businessModel.bars.get(0).galleryModel.background6.equals("")) {
            bitmapArrayList.add(GD.downloadUrl + GD.businessModel.bars.get(0).galleryModel.background6);
        }

        if (bitmapArrayList.size() == 1) {
            mViewPager.setPlayDelay(0);
            mViewPager.pause();
        }

        mViewPager.setAdapter(new ImageLoopAdapter(mViewPager, bitmapArrayList));
        if (bitmapArrayList.size() == 1) {
            mViewPager.pause();
        }

        bardetail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_businessmapview = new Intent(getApplicationContext(), BusinessMapViewActivity.class);
                goto_businessmapview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goto_businessmapview);
                overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
                finish();
            }
        });

        count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private class ImageLoopAdapter extends LoopPagerAdapter {

        ArrayList<String> mBitmapArrayList = new ArrayList<>();

        public ImageLoopAdapter(RollPagerView viewPager, ArrayList<String> bitmapArrayList1) {
            super(viewPager);
            viewPager.setAnimationDurtion(200);
            mBitmapArrayList = bitmapArrayList1 ;
            if (mBitmapArrayList.size() == 1){
                viewPager.pause();
            }
        }

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);

            if (mBitmapArrayList.size() > 0) {
                Picasso.with(getApplicationContext()).load(mBitmapArrayList.get(position)).into(view);
            }

            return view;
        }

        @Override
        public int getRealCount() {
            return mBitmapArrayList.size();
        }
    }

    private Bitmap resizeBitmap(Bitmap origin_bitmap){
        Matrix matrix = new Matrix();
        float scale = 1024/origin_bitmap.getWidth();
        float xTranslation = 0.0f, yTranslation = (720 - origin_bitmap.getHeight() * scale)/2.0f;
        RectF drawableRect = new RectF(0, 0, origin_bitmap.getWidth()-100,
                origin_bitmap.getHeight()-100);

        RectF viewRect = new RectF(0, 0, origin_bitmap.getWidth(),
                origin_bitmap.getHeight());

        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        matrix.setRotate(90, origin_bitmap.getWidth(), origin_bitmap.getHeight());
        matrix.postTranslate(xTranslation, yTranslation);
        matrix.preScale(scale, scale);
        return origin_bitmap ;
    }
    //swipe part

    @Override
    public void removeFirstObjectInAdapter() {
        dataList.remove(0);
        dummyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit(Object dataObject) {
        if (GD.slide_index == GD.businessModel.bars.get(0).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
        } else {
            GD.slide_index++;
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
        }
    }

    @Override
    public void onRightCardExit(Object dataObject) {
        if (GD.slide_index == GD.businessModel.bars.get(0).deals.size()-1) {
            int first_index = 0 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
        } else {
            GD.slide_index++;
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
    }

    @Override
    public void onScroll(float scrollProgressPercent) {
    }


    @OnClick(R.id.iv_right)
    public void right() {
        if (GD.slide_index > GD.businessModel.bars.get(0).deals.size()-1) {
            int first_index = GD.slide_index - 1 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 2);
        } else if (GD.slide_index == GD.businessModel.bars.get(0).deals.size() - 1) {
            cardsContainer.getTopCardListener().selectRight();
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size() + " deals"));
        } else {
            cardsContainer.getTopCardListener().selectRight();
            count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
        }
    }

    @OnClick(R.id.iv_left)
    public void left() {
        dataList = new ArrayList<>();
        if (GD.slide_index != 0) {
            int first_index = GD.slide_index - 1 ;
            int last_index = GD.businessModel.bars.get(0).deals.size() ;
            ResetCardViewData(first_index, last_index, 1);
        } else if ( GD.slide_index == 0){
            int first_index = 0 ;
            int last_index =  GD.businessModel.bars.get(0).deals.size();
            ResetCardViewData(first_index,last_index,2);
        }
    }

    private void ResetCardViewData(int first_index, int last_index, int flag) {
        for (int i = first_index; i < last_index; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.businessModel.bars.get(0).deals.get(i).deal_id;
            dealModel.deal_title = GD.businessModel.bars.get(0).deals.get(i).deal_title;
            dealModel.deal_description = GD.businessModel.bars.get(0).deals.get(i).deal_description;
            dealModel.deal_days = GD.businessModel.bars.get(0).deals.get(i).deal_days;
            dealModel.deal_duration = GD.businessModel.bars.get(0).deals.get(i).deal_duration;
            dealModel.deal_qty = GD.businessModel.bars.get(0).deals.get(i).deal_qty;
            dealModel.claimed = GD.businessModel.bars.get(0).deals.get(i).claimed;
            dealModel.in_wallet = GD.businessModel.bars.get(0).deals.get(i).in_wallet;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions;
            dealModel.index_number = i;
            dataList.add(dealModel);
        }
        if (flag == 1) {
            cardsContainer.undo();
            GD.slide_index--;
        } else if (flag == 2) {
            GD.slide_index = 0;
        }
        dummyAdapter = new DetailBarAdapter(this, dataList);
        cardsContainer.setAdapter(dummyAdapter);
        dummyAdapter.notifyDataSetChanged();
        count_page.setText(String.valueOf(GD.slide_index + 1) + " of " + String.valueOf(GD.businessModel.bars.get(0).deals.size()) + " deals");
    }

    private void setAdapters() {
        dummyAdapter = new DetailBarAdapter(this, dataList);
        cardsContainer.setAdapter(dummyAdapter);
    }

    private void setListeners() {
        cardsContainer.setSwipeListener(this);

    }

    private void getBarDeatilData() {
        dataList = new ArrayList<>();
        for (int i = 0 ; i < GD.businessModel.bars.get(0).deals.size() ; i++) {
            DealModel dealModel = new DealModel();
            dealModel.deal_id = GD.businessModel.bars.get(0).deals.get(i).deal_id ;
            dealModel.deal_title = GD.businessModel.bars.get(0).deals.get(i).deal_title ;
            dealModel.deal_description = GD.businessModel.bars.get(0).deals.get(i).deal_description ;
            dealModel.deal_days = GD.businessModel.bars.get(0).deals.get(i).deal_days ;
            dealModel.deal_duration = GD.businessModel.bars.get(0).deals.get(i).deal_duration ;
            dealModel.deal_qty = GD.businessModel.bars.get(0).deals.get(i).deal_qty ;
            dealModel.claimed = GD.businessModel.bars.get(0).deals.get(i).claimed ;
            dealModel.in_wallet = GD.businessModel.bars.get(0).deals.get(i).in_wallet ;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions ;
            dealModel.impressions = GD.businessModel.bars.get(0).deals.get(i).impressions ;
            dataList.add(dealModel);
        }
    }

    @Override
    public void onBackPressed() {
        Intent goto_businessmapview = new Intent(getApplicationContext(), BusinessMapViewActivity.class);
        goto_businessmapview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goto_businessmapview);
        overridePendingTransition(R.anim.forward_right_in, R.anim.forward_left_out);
        finish();
    }
}