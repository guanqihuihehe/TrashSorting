package com.szu.trashsorting.user.download;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.R;
import com.google.android.material.tabs.TabLayout;


public class ShowMyDownloadActivity extends BaseActivity {

    private int currentPageIndex =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmydownload);

        DownloadTypePagerAdapter downloadTypePagerAdapter = new DownloadTypePagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(downloadTypePagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("图片"));
        tabs.addTab(tabs.newTab().setText("视频"));
        tabs.addTab(tabs.newTab().setText("应用"));
        tabs.addTab(tabs.newTab().setText("文档"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // 被选中的时候
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPageIndex = tab.getPosition();
                viewPager.setCurrentItem(currentPageIndex);
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

        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
