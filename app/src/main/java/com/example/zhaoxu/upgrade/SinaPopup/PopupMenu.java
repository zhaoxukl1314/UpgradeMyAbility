package com.example.zhaoxu.upgrade.SinaPopup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.zhaoxu.upgrade.R;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoxukl1314 on 17/7/12.
 */

public class PopupMenu {

    private final int mScreenHeight;
    private List<PopMenuItem> mItems;
    private Activity mActivity;

    private static final int DEFAULT_HORIZONTAL_PADDING = 40;

    private static final int DEFAULT_VERTICAL_PADDING = 15;

    private static final int DEFAULT_TENSION = 40;

    private static final int DEFAULT_FRICTION = 5;

    private static final int DEFAULT_COLUMN_COUNT = 3;
    private final int mScreenWidth;
    private FrameLayout mAnimationLayout;
    private GridLayout mGridLayout;
    private boolean mIsShowing = false;
    private double mTension;
    private double mFriction;

    private SpringSystem mSpringSystem;

    {
        mSpringSystem = SpringSystem.create();
    }

    public PopupMenu(Builder builder) {
        mItems = builder.mMenuItems;
        mActivity = builder.mActivity;
        mTension = DEFAULT_TENSION;
        mFriction = DEFAULT_FRICTION;
        mScreenWidth = mActivity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = mActivity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
    }

    public void show() {
        showPopupMenu();
        if (mAnimationLayout.getParent() != null) {
            ViewGroup parent = (ViewGroup) mAnimationLayout.getParent();
            parent.removeView(mAnimationLayout);
        }

        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        decorView.addView(mAnimationLayout);

        showSubMenus(mGridLayout);

        mIsShowing = true;
    }

    private void showSubMenus(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View view = viewGroup.getChildAt(index);
                animateView(view, mScreenHeight, 0, mTension, mFriction);
            }
        }
    }

    private void animateView(final View view, int from, int to, double mTension, double mFriction) {
        Spring spring = mSpringSystem.createSpring();
        spring.setCurrentValue(from);
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(mTension, mFriction));
        spring.addListener(new SpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                view.setTranslationY((float) spring.getCurrentValue());
            }

            @Override
            public void onSpringAtRest(Spring spring) {

            }

            @Override
            public void onSpringActivate(Spring spring) {

            }

            @Override
            public void onSpringEndStateChange(Spring spring) {

            }
        });
        spring.setEndValue(to);
    }

    private void showPopupMenu() {
        mAnimationLayout = new FrameLayout(mActivity);
        mGridLayout = new GridLayout(mActivity);
        mGridLayout.setColumnCount(DEFAULT_COLUMN_COUNT);
        mGridLayout.setBackgroundColor(Color.parseColor("#f0ffffff"));
        mAnimationLayout.setBackgroundColor(Color.parseColor("#f0ffffff"));
        int leftMargin = (mScreenWidth - 3 * mActivity.getResources().getDimensionPixelSize(R.dimen.image_width)) / 4;

        for (int index = 0; index < mItems.size(); index++) {
            PopMenuItem item = mItems.get(index);
            PopSubView subView = new PopSubView(mActivity);
            subView.initItem(item);
            mGridLayout.addView(subView);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.leftMargin = leftMargin;
            if (index / DEFAULT_COLUMN_COUNT == 0) {
                layoutParams.topMargin = mScreenHeight / 3;
            } else {
                layoutParams.topMargin = 100;
            }
            subView.setLayoutParams(layoutParams);
        }

        mAnimationLayout.addView(mGridLayout);
        FrameLayout.LayoutParams gridLayoutParam = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        gridLayoutParam.gravity = Gravity.CENTER_VERTICAL;
        mGridLayout.setLayoutParams(gridLayoutParam);

        ImageButton imageButton = new ImageButton(mActivity);
        mAnimationLayout.addView(imageButton);
        FrameLayout.LayoutParams closeLayoutParams = new FrameLayout.LayoutParams(
                mActivity.getResources().getDimensionPixelSize(R.dimen.close_button_size),
                mActivity.getResources().getDimensionPixelSize(R.dimen.close_button_size)
        );
        closeLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        closeLayoutParams.bottomMargin = 100;
        imageButton.setLayoutParams(closeLayoutParams);
        imageButton.setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.tabbar_compose_background_icon_close));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePupMenu();
            }
        });

        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        decorView.addView(mAnimationLayout);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mAnimationLayout.setLayoutParams(flp);
    }

    public void closePupMenu() {
        if (mAnimationLayout != null) {
            if (mAnimationLayout.getParent() != null) {
                hideSubView(mGridLayout, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
                        viewGroup.removeView(mAnimationLayout);
                        mIsShowing = false;
                    }
                });
            }
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    private void hideSubView(ViewGroup v, AnimatorListenerAdapter listener) {
        int childCount = v.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View view = v.getChildAt(index);
            view.animate().translationY(mScreenHeight).setDuration(400).setListener(listener).start();
        }
    }

    public static class Builder {
        private List<PopMenuItem> mMenuItems = new ArrayList<>();
        private Activity mActivity;

        public Builder attachToActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder addItem(PopMenuItem item) {
            mMenuItems.add(item);
            return this;
        }

        public PopupMenu build() {
            return new PopupMenu(this);
        }

    }

    protected int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
