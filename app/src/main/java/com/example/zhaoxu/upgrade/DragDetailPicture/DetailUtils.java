package com.example.zhaoxu.upgrade.DragDetailPicture;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by zhaoxukl1314 on 17/7/20.
 */

public class DetailUtils {

    public static int dp2px(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }

    public static int getStatusHeight(Activity activity) {
        Rect decorRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(decorRect);
        return decorRect.top;
    }

    public static Rect getExitRect(Activity activity, View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int statusbarHeight = getStatusHeight(activity);
        rect.offset(0, -statusbarHeight);
        return rect;
    }
}
