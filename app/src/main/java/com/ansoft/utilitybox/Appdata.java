package com.ansoft.utilitybox;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by Abinash on 11/6/2016.
 */
public class Appdata {

    Drawable icon;
    String appName;
    long cacheSize;

    ApplicationInfo pckgInfo;

    public ApplicationInfo getPckgInfo() {
        return pckgInfo;
    }

    public void setPckgInfo(ApplicationInfo pckgInfo) {
        this.pckgInfo = pckgInfo;
    }

    File cacheDir;

    public File getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
