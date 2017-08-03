package com.example.zhaoxu.upgrade.DragDetailPicture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.zhaoxu.upgrade.R;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    private final static String TAG = "DetailActivity";

    public static void start(Activity activity, Rect exitRect, int resId) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("exit_rect", exitRect);
        intent.putExtra("image_res", resId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), resId, options);
        intent.putExtra("ratio", (float)(options.outWidth / options.outHeight));
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Rect exitRect = getIntent().getParcelableExtra("exit_rect");
        Log.d(TAG, "zhaoxu exitRect: " + exitRect);
        int imageRes = getIntent().getIntExtra("image_res", R.mipmap.corki_splash_0);
        float ratio = getIntent().getFloatExtra("ratio", 1.69f);
        final PhotoView photoView = (PhotoView) findViewById(R.id.image_photo_view);
        photoView.setImageResource(imageRes);

        final DragDetailView detailView = (DragDetailView) findViewById(R.id.drag_detail_view);
        detailView.setRatio(ratio);
        detailView.setExitZoomRect(exitRect);

        final DecimalFormat format = new DecimalFormat("#.#");
        photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                float scale = Float.parseFloat(format.format(photoView.getScale()));
                if (scale > 1) {
                    detailView.setEnabled(false);
                } else {
                    detailView.setEnabled(true);
                }
            }
        });

        detailView.setOnStatusChangeListener(new DragDetailView.OnStatusChangeListener() {
            @Override
            public void onPhotoMode() {
                photoView.setZoomable(true);
            }

            @Override
            public void onDetailMode() {
                photoView.setZoomable(false);
            }

            @Override
            public void onExit() {
                finish();
            }
        });

    }
}
