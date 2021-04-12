package com.szu.trashsorting.user.download;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.szu.trashsorting.R;

public class BigPicDialog extends Dialog{
    public String picPath;
    public ImageView imageView;
    public int width;

    public BigPicDialog(Context context, String path) {
        super(context, R.style.Theme_AppCompat_Dialog);
        picPath =path;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bigpic);
        imageView=findViewById(R.id.big_pic_item);
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        imageView.setImageBitmap(bitmap);
    }

}