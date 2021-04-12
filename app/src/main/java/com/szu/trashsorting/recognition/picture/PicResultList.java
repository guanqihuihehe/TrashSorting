package com.szu.trashsorting.recognition.picture;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PicResultList {
    public int code;
    public String msg;
    @SerializedName("newslist")//api返回格式限制，这里只能是newslist
    public List<PicResultEntity> picResultEntityList;

}
