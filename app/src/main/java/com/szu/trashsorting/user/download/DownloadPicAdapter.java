package com.szu.trashsorting.user.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.trashsorting.R;

import java.io.File;
import java.util.ArrayList;

public class DownloadPicAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;

    public ArrayList<String> picPaths=new ArrayList<String>();
    public ArrayList<File> picFiles=new ArrayList<File>();

    public DownloadPicAdapter(Context context, ArrayList<String> paths, ArrayList<File> picfiles){
        this.picPaths=paths;
        this.picFiles=picfiles;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return picFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return picFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.pic_item,null);
        ImageView iv = (ImageView) v.findViewById(R.id.iv_gridView_item);
        Bitmap bitmap = BitmapFactory.decodeFile(picPaths.get(position));
        iv.setImageBitmap(bitmap);
        return v;
    }
}