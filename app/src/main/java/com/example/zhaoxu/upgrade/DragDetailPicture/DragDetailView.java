package com.example.zhaoxu.upgrade.DragDetailPicture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.example.zhaoxu.upgrade.R;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by zhaoxukl1314 on 17/7/19.
 */

public class DragDetailView extends ScrollView {

    private final static String TAG = "DragDetailView";
    private final static int DEFAULT_IMAGE_REVERSE_HEIGHT = DetailUtils.dp2px(150);
    private final int DEFAULT_FULL_ANIM_DURATION = 250;
    private double mImageReverseHeight;
    private double mAnimationDuration;

    private final int INTENT_UNKNOW = -1;
    //用户此时的拖动意图为退出浏览
    private final int INTENT_EXIT = 0;
    //切换到详情模式
    private final int INTENT_DETAIL = 1;
    //切换到正常的图片浏览
    private final int INTENT_PHOTO = 2;
    //滚动查看详情
    private final int INTENT_SCROLL = 3;

    //图片模式
    private final int MODE_PHOTO = 0;
    //详情模式
    private final int MODE_DETAIL = 1;

    private int mMode;

    private int mIntenMode;
    private PhotoView mPhotoDetail;
    private ViewGroup mDetailContainer;
    private int mPhotoWidth;
    private float mPhotoHeight;
    private float mBottomTopMaring;
    private double mTransformToDetailDistance;
    private Rect mExitZoomRect;
    private OnStatusChangeListener mStatusChangeListener;
    private boolean mEnabled = true;
    private ViewGroup mDetailView;
    private float mLastX;
    private float mLastY;

    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();
    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator(1f);

    public DragDetailView(Context context) {
        super(context);
    }

    public DragDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DragDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DragDetailView);
        mImageReverseHeight = ta.getDimensionPixelSize(R.styleable.DragDetailView_photoReservedHeight, DEFAULT_IMAGE_REVERSE_HEIGHT);
        mAnimationDuration = ta.getInteger(R.styleable.DragDetailView_animationDuration, DEFAULT_FULL_ANIM_DURATION);
        ta.recycle();
        Log.d(TAG,"zhaoxu mImageReverseHeight: " + mImageReverseHeight);
        Log.d(TAG,"zhaoxu mAnimationDuration: " + mAnimationDuration);
        mMode = MODE_PHOTO;
        mIntenMode = INTENT_UNKNOW;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View wholeContainer = getChildAt(0);
        if (!(wholeContainer instanceof ViewGroup)) {
            throw new RuntimeException("child one should be viewgroup");
        }

        mPhotoDetail = (PhotoView) ((ViewGroup) wholeContainer).getChildAt(0);
        if (!(mPhotoDetail instanceof PhotoView)) {
            throw new RuntimeException("child one of whole container should be photoview");
        }

        mDetailContainer = (ViewGroup) ((ViewGroup) wholeContainer).getChildAt(1);
        mDetailView = (ViewGroup) mDetailContainer.getChildAt(0);
//        mPhotoDetail.setTranslationY((float) mImageReverseHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        mPhotoDetail.getLayoutParams().height = height;
        int transitionY = (int) (height - mImageReverseHeight);
        ((MarginLayoutParams)mDetailContainer.getLayoutParams()).topMargin = (int) mImageReverseHeight;
        mDetailContainer.setTranslationY(transitionY);
        mDetailView.setTranslationY(transitionY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRatio(final float ratio) {
        post(new Runnable() {
            @Override
            public void run() {
                if (getWidth() / getHeight() < ratio) {
                    mPhotoWidth = getWidth();
                    mPhotoHeight = mPhotoWidth / ratio;
                    mBottomTopMaring = (getHeight() - mPhotoHeight) / 2;
                } else {
                    mPhotoHeight = getHeight();
                    mPhotoWidth = (int) (mPhotoHeight * ratio);
                    mBottomTopMaring = 0;
                }
                mTransformToDetailDistance = getHeight() - mBottomTopMaring - mImageReverseHeight;
            }
        });
    }

    public void setExitZoomRect(Rect rect) {
        mExitZoomRect = rect;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if(!mEnabled) {
            return false;
        }
        switch (mMode) {
            case MODE_PHOTO:
                switch (mIntenMode) {
                    case INTENT_UNKNOW:
                        mPhotoDetail.setTranslationY(-deltaY + mPhotoDetail.getTranslationY());
                        if (deltaY > 0) {
                            mIntenMode = INTENT_DETAIL;
                            mDetailContainer.setTranslationY((float) (-deltaY + mTransformToDetailDistance));
                            mDetailView.animate().translationY(0).setDuration((long) mAnimationDuration).start();
                        } else {
                            mIntenMode = INTENT_EXIT;
                        }
                        deltaY = 0;
                        break;

                    case INTENT_EXIT:
                        deltaY = 0;
                        break;

                    case INTENT_DETAIL:
                    case INTENT_PHOTO:
                        if (isTouchEvent) {
                            onDrag(deltaY);
                            if (mPhotoDetail.getTranslationY() < 0) {
                                mIntenMode = INTENT_DETAIL;
                            } else {
                                mIntenMode = INTENT_PHOTO;
                            }
                        }
                        deltaY = 0;
                        break;
                }
                break;

            case MODE_DETAIL:
                switch (mIntenMode) {
                    case INTENT_UNKNOW:
                        if (scrollY == 0) {
                            if (isTouchEvent) {
                                if (deltaY < 0) {
                                    mIntenMode = INTENT_DETAIL;
                                    onDrag(deltaY);
                                    deltaY = 0;
                                } else {
                                    mIntenMode = INTENT_SCROLL;
                                }
                            } else {
                                deltaY = 0;
                            }
                        }
                        break;

                    case INTENT_SCROLL:
                        break;

                    case INTENT_PHOTO:
                    case INTENT_DETAIL:
                        onDrag(deltaY);
                        if (Math.abs(mPhotoDetail.getTranslationY()) >= mTransformToDetailDistance) {
                            mIntenMode = INTENT_DETAIL;
                        } else {
                            mIntenMode = INTENT_PHOTO;
                        }
                        deltaY = 0;
                    break;
                }
                break;
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mIntenMode == INTENT_EXIT) {
                    float deltaX = ev.getX() - mLastX;
                    float deltaY = ev.getY() - mLastY;
                    mLastX = ev.getX();
                    mLastY = ev.getY();
                    mPhotoDetail.setTranslationX(deltaX + mPhotoDetail.getTranslationX());
                    mPhotoDetail.setTranslationY(deltaY + mPhotoDetail.getTranslationY());
                    float moveRatio = mPhotoDetail.getTranslationY() / getHeight();
                    float scale = 1 - moveRatio;
                    if (scale > 1) {
                        scale = 1;
                    } else if (scale < 0){
                        scale = 0;
                    }

                    mPhotoDetail.setScaleX(scale);
                    mPhotoDetail.setScaleY(scale);
                    int alpha = (int) (255 * scale);
                    if (alpha > 255) {
                        alpha = 255;
                    } else if (alpha < 0){
                        alpha = 0;
                    }
                    setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                }
                break;

            case MotionEvent.ACTION_UP:
                switch (mMode) {
                    case MODE_DETAIL:
                        if (mIntenMode == INTENT_PHOTO) {
                            enterPhotoMode();
                        } else if (mIntenMode == INTENT_DETAIL) {
                            enterDetailMode();
                        }
                        break;

                    case MODE_PHOTO:
                        if (mIntenMode == INTENT_DETAIL) {
                            enterDetailMode();
                        } else if (mIntenMode == INTENT_PHOTO) {
                            enterPhotoMode();
                        } else if (mIntenMode == INTENT_EXIT) {
                            zoomToExit();
                        }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void zoomToExit() {
        float transtionX = mExitZoomRect.centerX() - getWidth() / 2;
        float transtionY = mExitZoomRect.centerY() - getHeight() / 2;
        float scale = Math.min(mExitZoomRect.width() / getWidth(), mExitZoomRect.height() / getHeight());
        ObjectAnimator transAnimationX = ObjectAnimator.ofFloat(mPhotoDetail, "translationX", transtionX);
        ObjectAnimator transAnimationY = ObjectAnimator.ofFloat(mPhotoDetail, "translationY", transtionY);
        ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(mPhotoDetail, "scaleX", scale);
        ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(mPhotoDetail, "scaleY", scale);
        ValueAnimator bgAnimation = ValueAnimator.ofInt(((ColorDrawable)getBackground()).getAlpha(), 0);
        bgAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setBackgroundColor(Color.argb((Integer) valueAnimator.getAnimatedValue(), 0, 0, 0));
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration((long) mAnimationDuration);
        animatorSet.setInterpolator(mDecelerateInterpolator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mStatusChangeListener.onExit();
                setVisibility(GONE);
            }
        });
        animatorSet.playTogether(transAnimationX, transAnimationY, scaleAnimationX, scaleAnimationY, bgAnimation);
        animatorSet.start();
    }

    private void enterPhotoMode() {
        mPhotoDetail.animate().translationY(0).setInterpolator(mOvershootInterpolator).setDuration((long) mAnimationDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMode = MODE_PHOTO;
                mIntenMode = INTENT_UNKNOW;
                if (mStatusChangeListener != null) {
                    mStatusChangeListener.onPhotoMode();
                }
            }
        }).start();
        int detailTranstionY = (int) (getHeight() - mImageReverseHeight);
        mDetailContainer.animate().translationY(detailTranstionY).setInterpolator(mOvershootInterpolator).setDuration((long) mAnimationDuration).start();
        mDetailView.animate().translationY(detailTranstionY).setInterpolator(mOvershootInterpolator).setDuration((long) mAnimationDuration).start();
    }

    private void enterDetailMode() {
        mPhotoDetail.animate().translationY(-(float) mTransformToDetailDistance).setDuration((long) mAnimationDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMode = MODE_DETAIL;
                mIntenMode = INTENT_UNKNOW;
                if (mStatusChangeListener != null) {
                    mStatusChangeListener.onDetailMode();
                }
            }
        }).start();
        mDetailContainer.animate().translationY(0).setDuration((long) mAnimationDuration).start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void onDrag(int deltaY) {
        mPhotoDetail.setTranslationY(-deltaY + mPhotoDetail.getTranslationY());
        mDetailContainer.setTranslationY(-deltaY + mDetailContainer.getTranslationY());
    }

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        mStatusChangeListener = onStatusChangeListener;
    }

    interface OnStatusChangeListener {
        void onPhotoMode();

        void onDetailMode();

        void onExit();
    }
}
