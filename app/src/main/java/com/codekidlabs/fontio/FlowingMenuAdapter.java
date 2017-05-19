package com.codekidlabs.fontio;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.codekidlabs.fontio.GeoTextView;
import com.codekidlabs.fontio.R;

import java.util.ArrayList;


public class FlowingMenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> mMenuTitle;


    // views
    ImageView mFlowingDrawable;
    GeoTextView mFlowingTitle;

    public FlowingMenuAdapter(Context context, ArrayList<String> mMenuTitle) {
        this.context = context;
        this.mMenuTitle = mMenuTitle;
    }

    @Override
    public int getCount() {
        return mMenuTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuTitle.get(position);
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

        mFlowingTitle.setText(mMenuTitle.get(position));
        mFlowingDrawable.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.currency_chf));

        return v;
    }
}
