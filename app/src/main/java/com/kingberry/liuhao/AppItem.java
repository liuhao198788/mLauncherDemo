package com.kingberry.liuhao;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/7/17.
 */

public class AppItem {

    private Drawable appIcon; // 存放图片
    private String appName; // 存放应用程序名
    private String pkgName; // 存放应用程序包名
    private boolean deletable = true;

    private String appMainAty;//应用的主Activity

    /**
     * 在第几页上
     */
    public int screenId = -1;

    /**true
     * 在屏幕上的第几列
     */
    public int itemPos = -1;

    public String toString() {
        return "AppItem{" +
                "appName='" + appName + '\'' +
                ", 在第=" + screenId +
                "页, 第=" + itemPos +
                "列}";
    }
    public AppItem(){

    }


    public AppItem(String appName, Drawable appIcon){
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public AppItem(String appName, Drawable appIcon, int screenId, int pos) {
        this.appName = appName;
        this.appIcon = appIcon;

        this.screenId = screenId;
        this.itemPos = pos;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppMainAty() {
        return appMainAty;
    }

    public void setAppMainAty(String appMainAty) {
        this.appMainAty = appMainAty;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }


}
