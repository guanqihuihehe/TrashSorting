package com.szu.trashsorting.common;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.szu.trashsorting.common.database.UserDBHelper;
import com.example.trashsorting.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.io.File;

import static com.szu.trashsorting.common.Constant.currentUserCollege;
import static com.szu.trashsorting.common.Constant.currentUserGrade;
import static com.szu.trashsorting.common.Constant.currentUserName;
import static com.szu.trashsorting.common.Constant.currentUserNickname;
import static com.szu.trashsorting.common.Constant.currentUserStuNo;
import static com.szu.trashsorting.common.Constant.currentIconFile;
import static com.szu.trashsorting.common.Constant.currentIconPath;
import static com.szu.trashsorting.common.Constant.currentUserFile;
import static com.szu.trashsorting.common.Constant.currentUserFileDirectory;
import static com.szu.trashsorting.common.Constant.currentUserPath;
import static com.szu.trashsorting.common.Constant.currentUserPicDirectory;
public class MainActivity extends BaseActivity {

    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        //设置导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_trashdetails, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        initUserInformation();
        initUserPath();
    }

    //申请权限
    public void requestPermission(){
        if	(ContextCompat.checkSelfPermission (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED   )	{
            ActivityCompat.requestPermissions(MainActivity.this,	new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE	},	1);
        }

        if(ContextCompat.checkSelfPermission (MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED   )	{
            ActivityCompat.requestPermissions(MainActivity.this,	new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE	},	1);
        }
        if(ContextCompat.checkSelfPermission (MainActivity.this,Manifest.permission.REQUEST_INSTALL_PACKAGES) !=PackageManager.PERMISSION_GRANTED   )	{
            ActivityCompat.requestPermissions(MainActivity.this,	new String[]{
                    Manifest.permission.REQUEST_INSTALL_PACKAGES	},	1);
        }
    }

    public void initUserInformation(){
        UserDBHelper dbHelper = new UserDBHelper(this, "user.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("User", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                String check_name = cursor.getString(cursor.getColumnIndex("name"));
                String check_password = cursor.getString(cursor.getColumnIndex("password"));
                String check_college = cursor.getString(cursor.getColumnIndex("college"));
                String check_grade = cursor.getString(cursor.getColumnIndex("grade"));
                String check_stu_no = cursor.getString(cursor.getColumnIndex("stu_no"));
                String check_nickname = cursor.getString(cursor.getColumnIndex("nickname"));

                if(check_name.equals(currentUserName))
                {
                    currentUserCollege =check_college;
                    currentUserGrade =check_grade;
                    currentUserNickname =check_nickname;
                    currentUserStuNo =check_stu_no;
                    Log.d(TAG,"用户信息："+ currentUserName +" "+ currentUserGrade +" "+ currentUserCollege +" "+ currentUserNickname +" "+ currentUserStuNo);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        currentUserFile = currentUserName;

    }

    public void initUserPath(){

        String appExternalDir;
        String userPath;
        String iconFile;
        String fileDirectory;
        String picDirectory;
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)){
            appExternalDir =Environment.getExternalStorageDirectory()+ File.separator+"TrashSorting";
            userPath =appExternalDir+ File.separator+ currentUserFile;
            (new File(userPath)).mkdirs();
            iconFile=userPath+File.separator+"MyIcon";
            (new File(iconFile)).mkdirs();
            fileDirectory=userPath+File.separator+"MyFile";
            (new File(fileDirectory)).mkdirs();
            picDirectory=userPath+File.separator+"MyPic";
            (new File(picDirectory)).mkdirs();
        }
        else{
            Log.d(TAG,"the device has not got a external storage");
            appExternalDir =null;
            iconFile=null;
            userPath=null;
            fileDirectory=null;
            picDirectory=null;
        }

        String iconPath=iconFile+File.separator+"myIcon.png";
        currentUserPath =userPath;
        currentIconFile =iconFile;
        currentIconPath =iconPath;
        currentUserFileDirectory =fileDirectory;
        currentUserPicDirectory =picDirectory;
        Log.d(TAG,"用户文件夹："+ currentUserPath);
        Log.d(TAG,"图标文件夹："+ currentIconFile);
        Log.d(TAG,"图标路径："+ currentIconPath);
        Log.d(TAG,"文件路径："+ currentUserFileDirectory);
        Log.d(TAG,"图片路径："+ currentUserPicDirectory);
    }


}

