package com.example.zhaoxu.upgrade.SinaPopup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhaoxu.upgrade.R;

/**
 * Created by zhaoxukl1314 on 17/7/11.
 */

public class PopSubView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public PopSubView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(mImageView, new LinearLayout.LayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.image_width),
                context.getResources().getDimensionPixelSize(R.dimen.image_width)));

        mTextView = new TextView(context);
        addView(mTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutParams lp = (LayoutParams) mTextView.getLayoutParams();
        lp.topMargin = 10;
        mTextView.setLayoutParams(lp);
    }

    public void initItem(PopMenuItem menuItem) {
        mImageView.setImageDrawable(menuItem.getIcon());
        mTextView.setText(menuItem.getTitle());
    }

    public PopSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
