package com.codekidlabs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.codekidlabs.fontio.GeoTextView;
import com.codekidlabs.fontio.R;


public class FlowingMenuAdapter extends BaseAdapter {

    private Context context;
    private String[] mMenuTitle;
    private int[] mMenuDrawable;


    // views
    ImageView mFlowingDrawable;
    GeoTextView mFlowingTitle;

    public FlowingMenuAdapter(Context context, String[] mMenuTitle, int[] mMenuDrawable) {
        this.context = context;
        this.mMenuTitle = mMenuTitle;
        this.mMenuDrawable = mMenuDrawable;
    }

    @Override
    public int getCount() {
        return mMenuTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return mMenuTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.flowing_menu_row, parent, false);

        mFlowingDrawable = (ImageView) v.findViewById(R.id.menu_icon);
        mFlowingTitle = (GeoTextView) v.findViewById(R.id.menu_title);

        mFlowingDrawable.setImageDrawable(ContextCompat.getDrawable(context, mMenuDrawable[position]));
        mFlowingTitle.setText(mMenuTitle[position]);

        return v;
    }
}
