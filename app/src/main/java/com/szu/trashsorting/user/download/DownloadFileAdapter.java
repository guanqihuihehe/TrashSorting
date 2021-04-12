package com.szu.trashsorting.user.download;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trashsorting.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class DownloadFileAdapter extends BaseAdapter {

    public List<File> downloadFiles;
    public DownloadFileAdapter(List<File> downloadFiles){
        this.downloadFiles = downloadFiles;
    }
    public int id;

    @Override
    public int getCount() {
        return downloadFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder= null;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=View.inflate(parent.getContext(),R.layout.file_item,null);
            viewHolder.fileIcon = convertView.findViewById(R.id.file_icon);
            viewHolder.fileName = convertView.findViewById(R.id.filename);
            viewHolder.fileSize = convertView.findViewById(R.id.filesize);
            viewHolder.fileTime = convertView.findViewById(R.id.downloadtime);
            convertView.setTag(viewHolder);//viewholder里面的数据放入converview
        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        File result_item= downloadFiles.get(position);

        String size=getPrintSize(result_item.length());

        Date data = new Date(result_item.lastModified());
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("YYYY年MM月DD日 HH:mm:ss");
        String dt=simpleDataFormat.format(data);

        viewHolder.fileName.setText(result_item.getName());
        viewHolder.fileTime.setText(dt);
        viewHolder.fileSize.setText(size);
        return convertView;
    }
    static class ViewHolder{
        TextView fileName, fileSize, fileTime;
        ImageView fileIcon;
    }

    private static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1000) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1000;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1000) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1000;
        }
        if (size < 1000) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1000;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
