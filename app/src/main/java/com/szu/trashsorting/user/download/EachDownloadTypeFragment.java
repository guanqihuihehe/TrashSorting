package com.szu.trashsorting.user.download;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.szu.trashsorting.R;

import java.io.File;
import java.util.ArrayList;

import static com.szu.trashsorting.common.Constant.currentUserFileDirectory;
import static com.szu.trashsorting.common.Constant.currentUserPicDirectory;

public class EachDownloadTypeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
    private final String TAG="DownloadTypeFragment";

    private ArrayList<String> downloadPaths =new ArrayList<>();
    private ArrayList<File> downloadFiles =new ArrayList<>();
    private int pageIndex;
    private Context downloadContext;

    public static EachDownloadTypeFragment newInstance(int index) {
        EachDownloadTypeFragment fragment = new EachDownloadTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageIndex =index;
        Log.d(TAG,"下载类型:"+ pageIndex);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=null;
        downloadContext=this.getActivity();
        if(pageIndex ==1){
            Log.d(TAG,"图片");
            downloadFiles.clear();
            downloadPaths.clear();
            root = inflater.inflate(R.layout.pic_fragment, container, false);
            GridView picGridView=root.findViewById(R.id.picGridView);
            File PicDir = new File(currentUserPicDirectory);
            File [] picFiles=PicDir.listFiles();
            for(int i = 0; i< (picFiles != null ? picFiles.length : 0); i++){
                String realPath=picFiles[i].getAbsolutePath();
                downloadPaths.add(realPath);
                this.downloadFiles.add(picFiles[i]);
            }
            for(int i = 0; i< downloadPaths.size(); i++){
                Log.d(TAG, downloadPaths.get(i));
            }
            picGridView.setAdapter(new DownloadPicAdapter(downloadContext, downloadPaths, this.downloadFiles));
            picGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BigPicDialog bigPicDialog=new BigPicDialog(downloadContext, downloadPaths.get(i));
                    bigPicDialog.setCanceledOnTouchOutside(true);
                    bigPicDialog.show();
                }
            });
        }
        if(pageIndex ==2){
            Log.d(TAG,"视频");
            root = inflater.inflate(R.layout.file_fragment, container, false);
        }
        if(pageIndex ==3){
            Log.d(TAG,"应用");
            downloadFiles.clear();
            downloadPaths.clear();
            root = inflater.inflate(R.layout.file_fragment, container, false);
            ListView appListView=root.findViewById(R.id.fileListView);
            File appDir = new File(currentUserFileDirectory);
            File [] appFiles=appDir.listFiles();
            for(int i = 0; i< (appFiles != null ? appFiles.length : 0); i++){
                String realPath=appFiles[i].getAbsolutePath();
                if(appFiles[i].getName().endsWith(".apk")){
                    downloadPaths.add(realPath);
                    downloadFiles.add(appFiles[i]);
                }
            }
            appListView.setAdapter(new DownloadFileAdapter(downloadFiles));
            appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String tempPath= downloadPaths.get(position);
                    File file=new File(tempPath);
                    install(file);
                }
            });
        }
        if(pageIndex ==4){
            Log.d(TAG,"文档");
            downloadFiles.clear();
            downloadPaths.clear();
            root = inflater.inflate(R.layout.file_fragment, container, false);
            ListView fileListView=root.findViewById(R.id.fileListView);
            File docDir = new File(currentUserFileDirectory);
            File [] docFiles=docDir.listFiles();
            for(int i = 0; i< (docFiles != null ? docFiles.length : 0); i++){
                String realPath=docFiles[i].getAbsolutePath();
                if(!docFiles[i].getName().endsWith(".apk")){
                    downloadPaths.add(realPath);
                    downloadFiles.add(docFiles[i]);
                }
            }
            fileListView.setAdapter(new DownloadFileAdapter(downloadFiles));
            fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String tempPath= downloadPaths.get(position);
                    File file=new File(tempPath);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever(); ;
                    String mime = "text/plain";

                    try {
                        mmr.setDataSource(tempPath);
                        mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    openFileByWps(file,mime);
                }
            });

        }

        return root;
    }

    public void install(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(downloadContext, "com.example.trashsorting.fileprovider", file);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        startActivity(intent);
    }

    public void openFileByWps(File file,String mime){
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(downloadContext, "com.example.trashsorting.fileprovider", file);
        intent.setDataAndType(fileUri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
    
}