package com.example.zhaoxu.upgrade.SinaPopup;

import android.graphics.drawable.Drawable;

/**
 * Created by zhaoxukl1314 on 17/7/11.
 */

public class PopMenuItem {

    private String mTitle;
    private Drawable mIcon;

    public PopMenuItem(Drawable mIcon, String mTitle) {
        this.mIcon = mIcon;
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
}
