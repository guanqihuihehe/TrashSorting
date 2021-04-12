package com.szu.trashsorting.common.web;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.trashsorting.R;
import com.szu.trashsorting.common.BaseActivity;
import com.szu.trashsorting.common.download.DownloadService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.szu.trashsorting.common.Constant.currentUserFileDirectory;
import static com.szu.trashsorting.common.Constant.currentUserPicDirectory;

public class WebActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar progress;
    private Context webActivityContext;
    private FrameLayout webViewContainer;
    private static final String TAG="WebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        initView();
        initListener();

        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //避免内存泄漏，对webView进行回收
        webViewContainer.removeView(webView);
        webView.removeAllViews();
        webView.destroy();
    }

    private void initView() {
        webView = new WebView(getApplicationContext());
        webViewContainer = findViewById(R.id.webViewContainer);
        webViewContainer.addView(webView);
        webActivityContext=this;
        progress = (ProgressBar) findViewById(R.id.progress);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){

            // 页面在当前页面跳转
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            // 页面加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG,"加载完成");
                super.onPageFinished(view, url);
                if(progress!=null){
                    progress.setVisibility(View.GONE);
                }
            }
        });

        //         网页加载进度显示
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(newProgress);
            }
        });
    }

    private void initListener() {

        // 长按点击事件
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"长按了");
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                int type=hitTestResult.getType();
                switch (type){
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // 选中的文字类型
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 　地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项

                        // 弹出保存图片的对话框
                        AlertDialog.Builder builder = new AlertDialog.Builder(webActivityContext);
                        builder.setTitle("提示");
                        builder.setMessage("保存图片到本地");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = hitTestResult.getExtra();
                                Log.d(TAG,"url："+url);

                                // 处理下载文件的名称
                                String[] split = url.split("/");
                                String nameString = split[split.length - 1];
                                if(!nameString.endsWith(".jpg")&&!nameString.endsWith(".png")&&!nameString.endsWith(".jpeg")){
                                    Date today=new Date();
                                    SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
                                    String dt=simpleDataFormat.format(today);
                                    nameString=dt+".png";
                                }
                                Log.d(TAG,"filename:"+nameString);

                                String pathname= currentUserPicDirectory + File.separator + nameString;
                                Intent intent = new Intent(WebActivity.this, DownloadService.class);
                                intent.putExtra("fileName",nameString);
                                intent.putExtra("downloadUrl",url);
                                intent.putExtra("destPath",pathname);
                                intent.putExtra("downloadType",1);
                                startService(intent);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            // 自动dismiss
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Button button1=dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        button1.setTextColor(Color.parseColor("#34ace0"));
                        Button button2=dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        button2.setTextColor(Color.parseColor("#333333"));
                        return true;
                    case WebView.HitTestResult.UNKNOWN_TYPE: //未知
                        break;
                }
                return false;
            }
        });

        //点击下载链接事件
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Log.d(TAG,"content:"+contentDisposition);
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                try {
                    fileName=URLEncoder.encode(fileName,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e(TAG,"文件名："+fileName);
                String destFile= currentUserFileDirectory +File.separator+fileName;
                Intent intent = new Intent(WebActivity.this, DownloadService.class);
                intent.putExtra("fileName",fileName);
                intent.putExtra("downloadUrl",url);
                intent.putExtra("destPath",destFile);
                intent.putExtra("downloadType",0);
                startService(intent);
            }
        });
    }

    // 监听返回键返回网页的上一层
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            if(webView != null){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
