package com.szu.trashsorting.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.szu.trashsorting.user.login.LoginActivity;

//import static com.szu.trashsorting.common.Constant.maincontext;

public class BaseActivity extends AppCompatActivity {

    private ForceOfflineReceiver receiver;
    private Context baseContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        baseContext=this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.trashsorting.FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
//        registerReceiver(receiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

     class ForceOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
            builder.setTitle("Warning");
            builder.setMessage("你确认退出登录吗？");
            builder.setCancelable(true);
            builder.setNegativeButton("取消",null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences current_user=baseContext.getSharedPreferences("current_user",  MODE_MULTI_PROCESS);
                    SharedPreferences.Editor  editor = current_user.edit();
                    editor.clear();
                    editor.apply();
                    ActivityCollector.finishAll(); // 销毁所有活动
                    Intent intent = new Intent(baseContext, LoginActivity.class);
                    baseContext.startActivity(intent); // 重新启动LoginActivity
                }
            });
            builder.show();
        }

    }

}
