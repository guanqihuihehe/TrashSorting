package com.szu.trashsorting.category;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.szu.trashsorting.R;
import com.szu.trashsorting.category.viewpager.TrashTypePagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class TrashCategoryFragment extends Fragment {

    private final static String TAG="TrashCategoryFragment";

    public TrashTypePagerAdapter trashTypePagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_classification, container, false);
        Context mainContext = this.getActivity();
        trashTypePagerAdapter = new TrashTypePagerAdapter(mainContext, getChildFragmentManager());
        final ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(trashTypePagerAdapter);

        TabLayout trashTypeTabs = root.findViewById(R.id.tabs);
        trashTypeTabs.addTab(trashTypeTabs.newTab().setText("可回收垃圾"));
        trashTypeTabs.addTab(trashTypeTabs.newTab().setText("有害垃圾"));
        trashTypeTabs.addTab(trashTypeTabs.newTab().setText("湿垃圾"));
        trashTypeTabs.addTab(trashTypeTabs.newTab().setText("干垃圾"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(trashTypeTabs));
        trashTypeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // 被选中的时候
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG,"current page:" + tab.getPosition());
            }

            // 没有被选中的时候
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            // 重现被选中的时候
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        return root;
    }
}