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

import com.szu.trashsorting.R;
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
        //????????????????????????webView????????????
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

            // ???????????????????????????
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            // ??????????????????
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG,"????????????");
                super.onPageFinished(view, url);
                if(progress!=null){
                    progress.setVisibility(View.GONE);
                }
            }
        });

        //         ????????????????????????
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

        // ??????????????????
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG,"?????????");
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                int type=hitTestResult.getType();
                switch (type){
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // ?????????????????????
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // ????????????
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // ??????Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // ???????????????
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // ?????????
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // ???????????????????????????
                    case WebView.HitTestResult.IMAGE_TYPE: // ??????????????????????????????

                        // ??????????????????????????????
                        AlertDialog.Builder builder = new AlertDialog.Builder(webActivityContext);
                        builder.setTitle("??????");
                        builder.setMessage("?????????????????????");
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = hitTestResult.getExtra();
                                Log.d(TAG,"url???"+url);

                                // ???????????????????????????
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
                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            // ??????dismiss
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
                    case WebView.HitTestResult.UNKNOWN_TYPE: //??????
                        break;
                }
                return false;
            }
        });

        //????????????????????????
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
                Log.e(TAG,"????????????"+fileName);
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

    // ???????????????????????????????????????
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
