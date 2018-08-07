package com.application.tippzi.Adapters;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;


import com.application.tippzi.Models.BaseListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by E on 6/28/2017.
 */

public class BaseListAdapter extends ArrayAdapter<BaseListInfo> {

    protected List<BaseListInfo> m_feedList;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context ctx, int resId) {
        super(ctx, resId);
        m_feedList = new ArrayList<BaseListInfo>();
        mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clearFeedList()
    {
        m_feedList.clear();
    }

    public void setFeedList(List<BaseListInfo> feedList) {
        m_feedList = feedList;
    }

    public void addFeedList(BaseListInfo feed) {
        m_feedList.add(feed);
    }

    public void addFeedList(int pos, BaseListInfo feed) {
        m_feedList.add(pos, feed);
    }

    public void deleteFeedList(int pos) {
        m_feedList.remove(pos);
    }

    public List<BaseListInfo> getAllItems()
    {
        return m_feedList;
    }

    @Override
    public int getCount() {
        return m_feedList.size();
    }

    @Override
    public BaseListInfo getItem(int position) {
        if(position < m_feedList.size())
            return m_feedList.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnItemSelectedListener {
    }

    public static class ViewHolderHelper
    {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View convertView, int id)
        {
            SparseArray<View> viewHolder = (SparseArray<View>)convertView.getTag();

            if(viewHolder == null)
            {
                viewHolder = new SparseArray<View>();
                convertView.setTag(viewHolder);
            }

            View childView = viewHolder.get(id);
            if(childView == null)
            {
                childView = convertView.findViewById(id);
                viewHolder.put(id, childView);
            }

            return (T) childView;
        }
    }

}
