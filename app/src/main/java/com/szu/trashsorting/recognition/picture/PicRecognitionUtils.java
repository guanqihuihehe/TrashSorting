package com.szu.trashsorting.recognition.picture;

import android.graphics.Bitmap;
import android.util.Log;

import com.szu.trashsorting.common.util.EncodeImageUtils;
import com.szu.trashsorting.category.viewpager.TrashTypeUtils;
import com.szu.trashsorting.recognition.HomeViewModel;
import com.szu.trashsorting.recognition.text.TextResultEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PicRecognitionUtils {

    private final static String TAG="PicRecognitionUtils";

    private final HomeViewModel homeViewModel;

    public PicRecognitionUtils(HomeViewModel homeViewModel){
        this.homeViewModel=homeViewModel;
    }

    public void startPicRecognition(Bitmap trashBitmap){

        EncodeImageUtils encodeImageUtils =new EncodeImageUtils();
        String trashBase64= encodeImageUtils.bitmapToBase64(trashBitmap);

        try {
            String res= requestPicRecognition(trashBase64);
            PicResultList picResultList=parseJsonWithGson(res);
            Log.d(TAG, String.valueOf(picResultList.picResultEntityList.size()));
            transformPicListToTextList(picResultList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transformPicListToTextList(PicResultList picResultList){
        if(picResultList.code==200){
            List<TextResultEntity> textResultEntityList =new ArrayList<>();
            for(int i = 0; i<picResultList.picResultEntityList.size(); i++){
                String resname=picResultList.picResultEntityList.get(i).name;
                int restype=picResultList.picResultEntityList.get(i).lajitype;
                if(restype<4){
                    TextResultEntity textResultEntity =new TextResultEntity();
                    textResultEntity.setName(resname);
                    textResultEntity.setType(restype);
                    textResultEntity.setAipre(0);
                    textResultEntity.setContain(TrashTypeUtils.contains[restype]);
                    textResultEntity.setExplain(TrashTypeUtils.explains[restype]);
                    textResultEntity.setTip(TrashTypeUtils.tips[restype]);
                    textResultEntityList.add(textResultEntity);
                }
            }
            homeViewModel.postTrashRecognitionList(textResultEntityList);
        }
        else {
            homeViewModel.postTrashRecognitionNull(0);
        }
    }


    public String requestPicRecognition(String code) throws Exception {

        String key = "9324ba6c5b972bfac106f5cf6c726fcf";  //你的apikey，天行数据控制台个人中心
        String base64Code = "data:image/jpeg;base64,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";
        String encode = URLEncoder.encode(code, "utf-8");

        //发送 POST 请求
        String sr = sendPost("http://api.tianapi.com/txapi/imglajifenlei/index", "key=" + key + "&img=" + encode);
        Log.d(TAG,sr);
        return sr;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;

        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            System.out.println(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public PicResultList parseJsonWithGson(final String requestText){
        Gson gson = new Gson();
        java.lang.reflect.Type jtype = new TypeToken<PicResultList>() {}.getType();
        return gson.fromJson(requestText, jtype);
    }

}
