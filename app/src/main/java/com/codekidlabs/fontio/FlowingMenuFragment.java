package com.codekidlabs.fontio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codekidlabs.FlowingMenuAdapter;


public class FlowingMenuFragment extends Fragment {

    private ListView mFlowingMenuList;

    private String[] mFlowingMenuTitle = {"hey", "hoye"};
    private int[] mFlowingMenuIcon = {R.drawable.alphabetical, R.drawable.shopping};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.flowing_menu_list, container, false);

        mFlowingMenuList = (ListView) root.findViewById(R.id.menu_list_view);

        mFlowingMenuList.setAdapter(new FlowingMenuAdapter(getActivity().getApplicationContext(), mFlowingMenuTitle, mFlowingMenuIcon));


        return root;
    }
}
