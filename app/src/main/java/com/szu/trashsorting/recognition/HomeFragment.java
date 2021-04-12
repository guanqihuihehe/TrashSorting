package com.szu.trashsorting.recognition;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.szu.trashsorting.R;
import com.szu.trashsorting.common.database.SearchRecordDBHelper;
import com.szu.trashsorting.recognition.picture.PicRecognitionUtils;
import com.szu.trashsorting.recognition.text.TextRecognitionUtils;
import com.szu.trashsorting.recognition.text.TextResultAdapter;
import com.szu.trashsorting.recognition.text.TextResultEntity;
import com.szu.trashsorting.recognition.text.TipsDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.szu.trashsorting.common.Constant.currentUserName;

public class HomeFragment extends Fragment {

    private final static String TAG="HomeFragment";

    private final int TAKE_PHOTO = 1;

    private int stateTrashImageView=View.GONE;
    private int stateNoResultTextView=View.GONE;
    private int stateResultListView=View.GONE;
    private int stateRecord=View.VISIBLE;



    private HomeViewModel homeViewModel;
    private List<TextResultEntity> tempTrashTextResultEntityList;
    private ImageView searchButton ;
    private ImageView scanButton ;
    private EditText searchEditText ;
    private TextView noResultTextView,recordTitle,recordTextView1,recordTextView2,recordTextView3,recordTextView4;
    private ImageView trashImageView;
    private ListView recognitionResultListView;

    private String trashPicPath;
    private Uri trashPicUri;
    private Context mainContext;
    private Bitmap trashBitmap=null;
    private ExecutorService executorService;

    private SearchRecordDBHelper searchRecordDBHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mainContext=this.getActivity();

        searchButton = root.findViewById(R.id.search_button);
        scanButton = root.findViewById(R.id.scan_button);
        searchEditText = root.findViewById(R.id.search_badge);
        noResultTextView = root.findViewById(R.id.tip);
        trashImageView=root.findViewById(R.id.trash_ImageView);
        recognitionResultListView =root.findViewById(R.id.result_section);
        recordTextView1=root.findViewById(R.id.record1);
        recordTextView2=root.findViewById(R.id.record2);
        recordTextView3=root.findViewById(R.id.record3);
        recordTextView4=root.findViewById(R.id.record4);
        recordTitle=root.findViewById(R.id.record_title);
        getRecord();

        //线程池启动线程
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        executorService= new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);

        //设置LifeCycle观察者
        homeViewModel.getTrashRecognitionList().observe(getViewLifecycleOwner(), new Observer<List<TextResultEntity>>() {
            @Override
            public void onChanged(@Nullable List<TextResultEntity> textResultEntityList) {

                if(textResultEntityList!=null){
                    stateResultListView=View.VISIBLE;
                    stateNoResultTextView=View.GONE;
                    stateRecord=View.GONE;
                    tempTrashTextResultEntityList = textResultEntityList;
                    TextResultAdapter textResultAdapter =new TextResultAdapter(textResultEntityList);
                    recognitionResultListView.setAdapter(textResultAdapter);
                }
                else {
                    stateResultListView=View.GONE;
                    stateNoResultTextView=View.VISIBLE;
                    stateRecord=View.GONE;
                }
                if(trashBitmap !=null){
                    trashImageView.setImageBitmap(trashBitmap);
                }
                noResultTextView.setVisibility(stateNoResultTextView);
                recognitionResultListView.setVisibility(stateResultListView);
                trashImageView.setVisibility(stateTrashImageView);
                recordTextView1.setVisibility(stateRecord);
                recordTextView2.setVisibility(stateRecord);
                recordTextView3.setVisibility(stateRecord);
                recordTextView4.setVisibility(stateRecord);
                recordTitle.setVisibility(stateRecord);
            }
        });

        initOnClickListener();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
        if(trashBitmap!=null){
            trashBitmap.recycle();
        }

    }

    public void initOnClickListener(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trashName=searchEditText.getText().toString();
                TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils(homeViewModel);
                textRecognitionUtils.startTextRecognition(trashName);
                stateTrashImageView=View.GONE;
                resetRecord(trashName);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTakePhoto();
            }
        });
        recordTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recordString=recordTextView1.getText().toString();
                if(!recordString.equals("")){
                    TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils(homeViewModel);
                    textRecognitionUtils.startTextRecognition(recordString);
                    stateTrashImageView=View.GONE;
                    resetRecord(recordString);
                }
            }
        });
        recordTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recordString=recordTextView2.getText().toString();
                if(!recordString.equals("")){
                    TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils(homeViewModel);
                    textRecognitionUtils.startTextRecognition(recordString);
                    stateTrashImageView=View.GONE;
                    resetRecord(recordString);
                }
            }
        });
        recordTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recordString=recordTextView3.getText().toString();
                if(!recordString.equals("")){
                    TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils(homeViewModel);
                    textRecognitionUtils.startTextRecognition(recordString);
                    stateTrashImageView=View.GONE;
                    resetRecord(recordString);
                }
            }
        });
        recordTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recordString=recordTextView4.getText().toString();
                if(!recordString.equals("")){
                    TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils(homeViewModel);
                    textRecognitionUtils.startTextRecognition(recordString);
                    stateTrashImageView=View.GONE;
                    resetRecord(recordString);
                }
            }
        });

        recognitionResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextResultEntity tempTextResultEntity = tempTrashTextResultEntityList.get(position);
                TipsDialog tipsDialog=new TipsDialog(mainContext, tempTextResultEntity);
                tipsDialog.setCanceledOnTouchOutside(true);
                tipsDialog.show();
            }
        });
    }

    public void startTakePhoto(){

        // 创建File对象，用于存储拍照后的图片
        File trashPic = new File(mainContext.getExternalCacheDir(), "trash_pic.png");
        trashPicPath =trashPic.toString();
        Log.d(TAG,"拍照的文件路径："+ trashPicPath);
        try {
            if(!trashPic.exists()){
                System.out.println("新建图片文件");
                trashPic.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            trashPicUri = Uri.fromFile(trashPic);
        } else {
            trashPicUri = FileProvider.getUriForFile(mainContext, "com.example.trashsorting.fileprovider", trashPic);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, trashPicUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    trashBitmap = BitmapFactory.decodeStream(mainContext.getContentResolver().openInputStream(trashPicUri));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                PicRecognitionUtils picRecognitionUtils=new PicRecognitionUtils(homeViewModel);
                                picRecognitionUtils.startPicRecognition(trashBitmap);
                                stateTrashImageView=View.VISIBLE;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void getRecord(){
        searchRecordDBHelper=new SearchRecordDBHelper(this.getActivity(),"record.db",null,2);
        SQLiteDatabase recordDB = searchRecordDBHelper.getWritableDatabase();

        Cursor cursor = recordDB.query("SearchRecord", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                String userName = cursor.getString(cursor.getColumnIndex("name"));

                String record1Text = cursor.getString(cursor.getColumnIndex("record1"));
                String record2Text = cursor.getString(cursor.getColumnIndex("record2"));
                String record3Text = cursor.getString(cursor.getColumnIndex("record3"));
                String record4Text = cursor.getString(cursor.getColumnIndex("record4"));

                if(userName.equals(currentUserName))
                {
                    recordTextView1.setText(record1Text);
                    recordTextView2.setText(record2Text);
                    recordTextView3.setText(record3Text);
                    recordTextView4.setText(record4Text);
                    Log.d(TAG,"数据："+record1Text+" "+record2Text+" "+record3Text+" "+record4Text);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void resetRecord(String newSearchName){
        String temp=newSearchName.replace(" ","");
        if(newSearchName.equals("")||temp.equals("")){
            return;
        }
        searchRecordDBHelper=new SearchRecordDBHelper(this.getActivity(),"record.db",null,2);
        SQLiteDatabase recordDB = searchRecordDBHelper.getWritableDatabase();

        Cursor cursor = recordDB.query("SearchRecord", null, null, null, null, null, null);
        String record1Text="",record2Text="",record3Text="",record4Text="";

        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                String userName = cursor.getString(cursor.getColumnIndex("name"));

                record1Text = cursor.getString(cursor.getColumnIndex("record1"));
                record2Text = cursor.getString(cursor.getColumnIndex("record2"));
                record3Text = cursor.getString(cursor.getColumnIndex("record3"));
                record4Text = cursor.getString(cursor.getColumnIndex("record4"));

                if(userName.equals(currentUserName))
                {
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        if(!newSearchName.equals(record1Text)&&!newSearchName.equals(record2Text)&&!newSearchName.equals(record3Text)&&!newSearchName.equals(record4Text))
        {
            ContentValues values = new ContentValues();
            //修改条件
            String whereClause = "name=?";
            //修改添加参数
            String[] whereArgs={currentUserName};
            values.put("record1",newSearchName);
            values.put("record2",record1Text);
            values.put("record3",record2Text);
            values.put("record4",record3Text);
            recordDB.update("SearchRecord",values,whereClause,whereArgs);
        }
    }
}