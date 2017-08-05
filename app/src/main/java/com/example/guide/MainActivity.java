package com.example.guide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button targetRight;
    private Button targetMid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        targetRight = (Button) findViewById(R.id.bt_top_right);
        targetMid = (Button) findViewById(R.id.bt_middle);
        targetRight.setOnClickListener(this);
        targetMid.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        GuideView.builder(this)
                .addHighLight(targetRight, R.drawable.arrow_down_right, R.drawable.tip, GuideView.GRAVITY_LEFT_TOP)
                .addHighLight(targetMid, R.drawable.arrow_up_left, R.drawable.tip, GuideView.GRAVITY_BOTTOM, GuideView.SHAPE_RECT)
                .show();
    }
}
