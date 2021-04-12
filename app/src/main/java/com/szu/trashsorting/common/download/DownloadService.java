package com.szu.trashsorting.common.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.trashsorting.R;

import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {

    private final static String TAG="DownloadService";

    public HashMap<Integer,DownloadTask> downloadTaskHashMap;

    public BroadcastReceiver notificationBroadcastReceiver =new BroadcastReceiver (){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int downloadID = intent.getIntExtra("download_id", -1);
            if (downloadID != -1) {
                if (action.equals("notification_clicked")) {
                    //处理点击事件
                    pauseDownload(downloadID);
                    Log.e(TAG,"进入广播："+downloadID);
                }
                if (action.equals("notification_cancelled")) {
                    //处理滑动清除和点击删除事件
//                    Toast.makeText(context, "取消下载", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    class DownloadListener implements DownloadListenerInterface{

        private final int downloadType;
        private final int downloadID;
        private final String downloadName;
        private final String destPath;

        DownloadListener(int downloadID,String downloadName,int downloadType,String downloadUrl,String destPath){
            this.downloadID = downloadID;
            this.downloadName=downloadName;
            this.downloadType=downloadType;
            this.destPath=destPath;
            getNotificationManager().notify(downloadID, getNotification("正在下载..."+downloadName, 0,downloadID));
        }
        @Override
        public void onProgress(int progress) {
            Log.e(TAG,"更新进度："+downloadID);
            getNotificationManager().notify(downloadID, getNotification("正在下载..."+downloadName, progress,downloadID));
        }

        @Override
        public void onSuccess() {
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(downloadID, getNotification("成功下载:"+downloadName, -1,downloadID));
            Toast.makeText(DownloadService.this, "成功下载:"+downloadName, Toast.LENGTH_SHORT).show();
            Toast.makeText(DownloadService.this,"文件保存路径:"+destPath,Toast.LENGTH_LONG).show();
            if(downloadType==1){
                // 最后通知图库更新
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + destPath)));
            }
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
        }

        @Override
        public void onFailed() {
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(downloadID, getNotification("下载失败"+downloadName, -1,downloadID));
            Toast.makeText(DownloadService.this, "下载失败"+downloadName, Toast.LENGTH_SHORT).show();
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
        }

        @Override
        public void onPaused() {
            Log.e(TAG,"暂停下载："+downloadID);
            getNotificationManager().notify(downloadID, getNotification("暂停下载"+downloadName, -1,downloadID));
        }

        @Override
        public void onCanceled() {
            stopForeground(true);
            Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadTaskHashMap=new HashMap<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction("notification_clicked");
        registerReceiver(notificationBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int downloadID =startId;
        String destPath=intent.getStringExtra("destPath");
        int downloadType=intent.getIntExtra("downloadType",-1);
        String downloadUrl=intent.getStringExtra("downloadUrl");
        String downloadName = intent.getStringExtra("fileName");
        if(downloadTaskHashMap.containsKey(downloadID)){
            downloadTaskHashMap.get(downloadID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,downloadUrl,destPath);
        }
        else {
            DownloadListener listener=new DownloadListener(downloadID,downloadName,downloadType,downloadUrl,destPath);
            DownloadTask downloadTask = new DownloadTask(listener);
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,downloadUrl,destPath);
            downloadTaskHashMap.put(downloadID,downloadTask);
            startForeground(downloadID, getNotification("正在下载..."+ downloadName, 0,downloadID));
            Toast.makeText(DownloadService.this, "正在下载..."+ downloadName, Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(notificationBroadcastReceiver);
        for(Map.Entry<Integer,DownloadTask> entry:downloadTaskHashMap.entrySet()){
            entry.getValue().cancelDownload();
        }
        super.onDestroy();
    }

    public void pauseDownload(int downloadID) {
        if(downloadTaskHashMap.containsKey(downloadID)){
            DownloadTask originTask=downloadTaskHashMap.get(downloadID);
            assert originTask != null;
            originTask.pauseDownload();
            if(!originTask.isPaused()){
                Log.e(TAG,"重启："+downloadID);
                DownloadTask downloadTask = new DownloadTask(originTask.getListener());

                downloadTaskHashMap.remove(downloadID);
                downloadTaskHashMap.put(downloadID,downloadTask);
                startForeground(downloadID, getNotification("重新下载：", 0,downloadID));
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,originTask.currentUrl,originTask.currentDestPath);
            }
            else {
                Log.e(TAG,"暂停："+downloadID);
            }

        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress,int downloadID) {
        String CHANNEL_ONE_ID = String.valueOf(downloadID);
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);//用这个方法注册这个notification到系统中
        }
//        Intent intent = new Intent(this, ShowMyDownloadActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        //点击广播监听
        Intent intentClick = new Intent();
        intentClick.setAction("notification_clicked");
        intentClick.putExtra("download_id", downloadID);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
        //cancle广播监听
        Intent intentCancel = new Intent();
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra("download_id", downloadID);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setContentIntent(pendingIntentClick);
        builder.setDeleteIntent(pendingIntentCancel);
        builder.setChannelId(CHANNEL_ONE_ID);
        builder.setAutoCancel(true);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }
}
