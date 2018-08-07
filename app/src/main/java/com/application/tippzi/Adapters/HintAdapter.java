package com.application.tippzi.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.application.tippzi.R;

import java.util.List;

/**
 * Created by E on 6/26/2017.
 */

public class HintAdapter <T> extends ArrayAdapter<T> {
    private static final String TAG = HintAdapter.class.getSimpleName();

    private static final int DEFAULT_LAYOUT_RESOURCE = R.layout.control_spinner;

    private int layoutResource;
    private String hintResource;
    private boolean mSelect_flag ;
    private Context mContext;

    private final LayoutInflater layoutInflater;

    public HintAdapter(Context context, int hintResource, List<T> data, boolean select_flag) {
        this(context, DEFAULT_LAYOUT_RESOURCE, context.getString(hintResource), data, select_flag);
    }

    public HintAdapter(Context context, String hint, List<T> data, boolean select_flag) {
        this(context, DEFAULT_LAYOUT_RESOURCE, hint, data, select_flag);
    }

    public HintAdapter(Context context, int layoutResource, int hintResource, List<T> data, boolean select_flag) {
        this(context, layoutResource, context.getString(hintResource), data, select_flag);
    }

    public HintAdapter(Context context, int layoutResource, String hintResource, List<T> data, boolean select_flag) {
        // Create a copy, as we need to be able to add the hint without modifying the array passed in
        // or crashing when the user sets an unmodifiable.
        super(context, layoutResource, data);
        this.mContext = context ;
        this.layoutResource = layoutResource;
        this.hintResource = hintResource;
        this.mSelect_flag = select_flag ;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * Hook method to set a custom view.
     *
     * Provides a default implementation using the simple spinner dropdown item.
     *
     * @param position Position selected
     * @param convertView View
     * @param parent Parent view group
     */

    protected View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = inflateDefaultLayout(parent);
        Object item = getItem(position);
        TextView textView = view.findViewById(R.id.spinnerTarget);
        if (mSelect_flag) {
            textView.setBackgroundResource(R.drawable.rect_edittext_active);
            textView.setTextColor(mContext.getResources().getColor(R.color.Dark));
        } else {
            textView.setBackgroundResource(R.drawable.rect_edittext_black_inactive);
            textView.setTextColor(mContext.getResources().getColor(R.color.Dark));
            textView.setHintTextColor(mContext.getResources().getColor(R.color.Dark));
        }
        textView.setText(item.toString());
        textView.setHint("");
        return view;
    }

    private View inflateDefaultLayout(ViewGroup parent) {
        return inflateLayout(DEFAULT_LAYOUT_RESOURCE, parent, false);
    }

    private View inflateLayout(int resource, ViewGroup root, boolean attachToRoot) {
        return layoutInflater.inflate(resource, root, attachToRoot);
    }

    public View inflateLayout(ViewGroup root, boolean attachToRoot) {
        return layoutInflater.inflate(layoutResource, root, attachToRoot);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "position: " + position + ", getCount: " + getCount());
        View view;
        if (position == getHintPosition()) {
            view = getDefaultView(parent);
        } else {
            view = getCustomView(position, convertView, parent);
        }
        return view;
    }

    private View getDefaultView(ViewGroup parent) {
        View view = inflateDefaultLayout(parent);
        TextView textView = view.findViewById(R.id.spinnerTarget);
        if (mSelect_flag) {
            textView.setBackgroundResource(R.drawable.rect_edittext_active);
            textView.setTextColor(mContext.getResources().getColor(R.color.Dark));
            //textView.setHintTextColor(mContext.getResources().getColor(R.color.Dark));
        } else {
            textView.setBackgroundResource(R.drawable.rect_edittext_black_inactive);
            textView.setTextColor(mContext.getResources().getColor(R.color.Dark));
            textView.setHintTextColor(mContext.getResources().getColor(R.color.Dark));
        }
        textView.setText("");
        textView.setHint(hintResource);

        return view;
    }

    /**
     * Gets the position of the hint.
     *
     * @return Position of the hint
     */
    public int getHintPosition() {
        int count = getCount();
        return count > 0 ? count + 1 : count;
    }
}
