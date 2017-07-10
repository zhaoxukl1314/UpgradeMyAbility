package com.example.zhaoxu.upgrade.Vector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.zhaoxu.upgrade.R;

public class VectorActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
    }
}
