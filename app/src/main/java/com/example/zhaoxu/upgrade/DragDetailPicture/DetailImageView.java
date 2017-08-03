package com.example.zhaoxu.upgrade.DragDetailPicture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by zhaoxukl1314 on 17/7/19.
 */

public class DetailImageView extends ImageView {
    public DetailImageView(Context context) {
        super(context);
    }

    public DetailImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(size,size);
        } else {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }
}
