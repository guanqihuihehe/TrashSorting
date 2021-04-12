package com.szu.trashsorting.common.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpRequestUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        System.out.println("address:"+address);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
