package com.jess.contactfiltergroup;

import android.graphics.drawable.Drawable;

/**
 * Created by USER on 12/17/2018.
 */

public class ApplicationObj {

    private String appId;
    private String appName;
    private String appPackageName;
    private Drawable appIcon;

    public ApplicationObj(String name, String packageName, Drawable icon){
        this.appName = name;
        this.appPackageName = packageName;
        this.appIcon = icon;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
