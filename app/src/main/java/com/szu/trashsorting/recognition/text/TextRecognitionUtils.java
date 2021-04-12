package com.szu.trashsorting.recognition.text;

import android.util.Log;

import com.szu.trashsorting.common.util.HttpRequestUtil;
import com.szu.trashsorting.recognition.HomeViewModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TextRecognitionUtils {

    private List<TextResultEntity> UtilsTrashList = new ArrayList<>();
    private final String TAG = "TextRecognitionUtils";
    private final HomeViewModel homeViewModel;
    private String mTrashName;

    public TextRecognitionUtils(HomeViewModel homeViewModel){
        this.homeViewModel=homeViewModel;
    }

    public void startTextRecognition(String trashName)
    {
        mTrashName =trashName;

        //线程池启动线程
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        ExecutorService executorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                requestTextRecognitionResult(mTrashName);
            }
        });

    }

    /**
     * 传入垃圾名称，向api发送垃圾识别请求
     */
    public void requestTextRecognitionResult(String trashName){

        String address = setAddress(trashName);

        HttpRequestUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "请求识别失败，http连接失败");
                homeViewModel.postTrashRecognitionNull(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d(TAG,"返回的内容："+responseText);
                final TextResultList textResultList = parseJsonWithGson(responseText);
                if(textResultList.textResultEntityList !=null&& textResultList.textResultEntityList.size()!=0){
                    final int code = textResultList.code;
                    if (code == 200){
                        Log.e(TAG, "请求识别成功");
                        UtilsTrashList= textResultList.textResultEntityList;
                        homeViewModel.postTrashRecognitionList(UtilsTrashList);
                    }else{
                        Log.e(TAG, "请求识别失败，状态码不是200");
                        homeViewModel.postTrashRecognitionNull(0);
                    }
                }
                else {
                    Log.e(TAG, "请求识别失败，没有解析出EntityList");
                    homeViewModel.postTrashRecognitionNull(0);
                }
            }

        });
    }

    /**
     * 根据垃圾名称设置链接地址
     */
    private String setAddress(String trashName){
        String address = "http://api.tianapi.com/txapi/lajifenlei/index?key=025cbec0565f78c109edc83fddd527ce&word=纸巾";
        address = address.replaceAll("纸巾",trashName);
        return address;
    }

    /*
    *  解析返回的json报文内容
    */
    public TextResultList parseJsonWithGson(final String requestText){
        Gson gson = new Gson();
        return gson.fromJson(requestText, TextResultList.class);
    }


}
