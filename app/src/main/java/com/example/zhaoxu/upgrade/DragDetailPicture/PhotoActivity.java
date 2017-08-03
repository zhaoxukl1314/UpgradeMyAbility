package com.example.zhaoxu.upgrade.DragDetailPicture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zhaoxu.upgrade.R;

public class PhotoActivity extends AppCompatActivity {

    private int[] mIcons;
    private RecyclerView mPhotoGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mIcons = new int[]{
                R.mipmap.corki_splash_0,
                R.mipmap.corki_splash_1,
                R.mipmap.corki_splash_2,
                R.mipmap.corki_splash_3,
                R.mipmap.corki_splash_4,
                R.mipmap.corki_splash_5,
                R.mipmap.corki_splash_6,
                R.mipmap.corki_splash_7,
                R.mipmap.corki_splash_8
        };
        mPhotoGrid = (RecyclerView) findViewById(R.id.reccycler_photo_view);
        mPhotoGrid.setLayoutManager(new GridLayoutManager(this,2));
        mPhotoGrid.setAdapter(new RecyclerAdapter());
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.drag_detail_image_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final int imageRes = mIcons[position];
            holder.mImageView.setImageDrawable(getResources().getDrawable(imageRes));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetailActivity.start(PhotoActivity.this, DetailUtils.getExitRect(PhotoActivity.this, view), imageRes);
                    Toast.makeText(PhotoActivity.this, "点击了"+position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mIcons.length;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private DetailImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (DetailImageView) itemView.findViewById(R.id.detail_image);
        }
    }

}
