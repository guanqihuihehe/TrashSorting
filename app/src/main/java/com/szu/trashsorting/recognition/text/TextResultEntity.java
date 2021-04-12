package com.szu.trashsorting.recognition.text;

public class TextResultEntity {
    public String name;
    public int type;
    public int aipre;
    public String explain;
    public String contain;
    public String tip;

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getAipre() {
        return aipre;
    }

    public String getExplain() {
        return explain;
    }

    public String getContain() {
        return contain;
    }

    public String getTip() {
        return tip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAipre(int aipre) {
        this.aipre = aipre;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContain(String contain) {
        this.contain = contain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }


    public void setTip(String tip) {
        this.tip = tip;
    }
}
