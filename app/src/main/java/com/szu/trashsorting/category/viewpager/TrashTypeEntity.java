package com.szu.trashsorting.category.viewpager;

public class TrashTypeEntity {

    public int picture;
    public String url;
    public String title;
    public String explain;
    public String contain;
    public String tip;

    public String getTitle() {
        return title;
    }

    public int getPicture() {
        return picture;
    }

    public String getContain() {
        return contain;
    }

    public String getExplain() {
        return explain;
    }

    public String getTip() {
        return tip;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContain(String contain) {
        this.contain = contain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
