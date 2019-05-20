package com.neusoft.bean.po;

public class Crumb {

    private String valueName;

    private String urlParam;

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    public Crumb() {
    }

    public Crumb(String valueName, String urlParam) {
        this.valueName = valueName;
        this.urlParam = urlParam;
    }

    @Override
    public String toString() {
        return "Crumb{" +
                "valueName='" + valueName + '\'' +
                ", urlParam='" + urlParam + '\'' +
                '}';
    }
}
