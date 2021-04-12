package com.szu.trashsorting.user.information;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.util.RoundBitmapUtil;
import com.example.trashsorting.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.szu.trashsorting.common.Constant.currentIconPath;

public class ChangeIconActivity extends BaseActivity {

    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_PHOTO = 2;

    private final String TAG="ChangeIconActivity";

    private ImageView myIconPic;
    private Uri imageUri;

    private String tempIconPath= currentIconPath;
    private String photoPath;
    private Bitmap myIconBitmap;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemyinfo);

        //线程池启动线程
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        executorService= new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);

        Button chooseFromTakingPhoto = (Button) findViewById(R.id.take_photo);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        Button cancelButton =(Button) findViewById(R.id.cancle2);
        Button confirmButton = (Button) findViewById(R.id.make_sure);
        myIconPic = (ImageView) findViewById(R.id.picture);

        myIconBitmap = BitmapFactory.decodeFile(currentIconPath);
        myIconPic.setImageBitmap(myIconBitmap);
        
        chooseFromTakingPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"目前的图片路径："+tempIconPath);
                Log.d(TAG,"待储存到的图片路径："+ currentIconPath);
                copyImage(tempIconPath, currentIconPath);
                Log.d(TAG,"最终的图片路径："+ currentIconPath);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void takePhoto(){
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        photoPath =outputImage.toString();
        Log.d(TAG,"拍照的文件路径："+ photoPath);
        try {
            if(!outputImage.exists()){
                outputImage.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(ChangeIconActivity.this, "com.example.trashsorting.fileprovider", outputImage);
        }
        
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);// 打开相机
    }

    private void openAlbum() {
        if (ContextCompat.checkSelfPermission(ChangeIconActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChangeIconActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Log.d(TAG,"拍摄的图片的uri："+imageUri);
                        tempIconPath= photoPath;
                        Bitmap bitmap1 = BitmapFactory.decodeFile(tempIconPath);
                        myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap1);
                        bitmap1.recycle();
                        myIconPic.setImageBitmap(myIconBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //其实是以4.4之前的方式储存的uri
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.d(TAG,"内存的图片的路径："+path);
        tempIconPath=path;
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Log.d(TAG,"展示的路径："+imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            myIconBitmap= RoundBitmapUtil.toRoundBitmap(bitmap);
            bitmap.recycle();
            myIconPic.setImageBitmap(myIconBitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public  void copyImage(String old_Path, String new_Path) {
        final String oldPath=old_Path,newPath=new_Path;

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    File oldfile = new File(oldPath);
                    if (oldfile.exists()) {//文件存在时
                        InputStream inStream = new FileInputStream(oldPath);//读入原文件
                        FileOutputStream fs = new FileOutputStream(newPath);
                        byte[] buffer = new byte[1444];
                        int length;
                        int value = 0 ;
                        while ((byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread;//字节数 文件大小
                            value ++ ;  //计数
                            fs.write(buffer, 0, byteread);
                        }
                        inStream.close();
                    }
                    else {
                        Log.d(TAG,"路径不存在");
                    }
                } catch (Exception e) {
                    Log.d(TAG,"复制单个文件操作出错");
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "您已拒绝权限申请，请开启内存写入权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(myIconBitmap!=null){
            myIconBitmap.recycle();
        }
    }
}