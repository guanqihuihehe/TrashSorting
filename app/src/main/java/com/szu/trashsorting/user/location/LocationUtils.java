package com.szu.trashsorting.user.location;

import android.util.Log;

import com.szu.trashsorting.common.util.HttpRequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationUtils {

    private LocationViewModel mLocationViewModel;
    private final String TAG="LocationUtils";

    public void getLocation (LocationViewModel locationViewModel, String latitude, String longitude) {
        mLocationViewModel=locationViewModel;
        final String string=latitude+","+longitude;

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
                requestAddress(string);
            }
        });
    }

    /**
     * 请求处理数据
     */
    public void requestAddress(String str){
        // 根据返回到的 URL 链接进行申请和返回数据
        String url="http://api.map.baidu.com/geocoder?output=json&location="+str+"&ak=tVhIGUx3mG7ey3rghsvFKXYnYtWiqyEv";
        HttpRequestUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG,"http请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                Log.d(TAG,"响应内容："+responseText);
                LocationEntity locationEntity = parseJsonWithGson(responseText);
                Log.d(TAG,"status："+ locationEntity.status);

                if(locationEntity !=null){
                    String code = (locationEntity.status);
                    String city = locationEntity.result.addressComponent.city;
                    String address = locationEntity.result.formatted_address;

                    if (code.equals("OK")){
                        Log.e(TAG,"请求地址成功");
                        Log.d(TAG,city+" "+address);
                        mLocationViewModel.postGPSCity(city);
                        mLocationViewModel.postGPSAddress(address);
                    }
                    else{
                        Log.e(TAG,"失败，状态码："+code);
                    }
                }
                else {
                    Log.e(TAG,"失败，解析内容为空");
                }
            }
        });
    }

    public static LocationEntity parseJsonWithGson(final String requestText){
        Gson gson = new Gson();
        java.lang.reflect.Type jtype = new TypeToken<LocationEntity>() {}.getType();
        return gson.fromJson(requestText, jtype);
    }
}
