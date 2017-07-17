package com.example.zhaoxu.upgrade.SinaPopup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.zhaoxu.upgrade.R;

public class SinaPopupActivity extends Activity implements View.OnClickListener{

    private RelativeLayout mAddButtonLayout;
    private PopupMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sina_popup);
        mAddButtonLayout = (RelativeLayout) findViewById(R.id.add_button);
        mAddButtonLayout.setOnClickListener(this);
        mMenu = new PopupMenu.Builder()
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_headlines),"头条"))
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_idea),"理想"))
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_lbs),"嗯嗯"))
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_more),"更多"))
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_photo),"图片"))
                .addItem(new PopMenuItem(getResources().getDrawable(R.mipmap.tabbar_compose_review),"检查"))
                .attachToActivity(this)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button:
                if (!mMenu.isShowing()) {
                    mMenu.show();
                }
                break;

            default:
                break;
        }
    }
}
