package com.szu.trashsorting.recognition.text;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TextResultList {
    public int code;
    public String msg;
    @SerializedName("newslist")//api返回格式限制，这里只能是newslist
    public List<TextResultEntity> textResultEntityList;
}
