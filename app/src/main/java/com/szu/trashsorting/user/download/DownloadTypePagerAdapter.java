package com.szu.trashsorting.user.download;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DownloadTypePagerAdapter extends FragmentPagerAdapter {


    public DownloadTypePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return EachDownloadTypeFragment.newInstance(position + 1);
    }
    @Override
    public int getCount() {

        return 4;
    }

}