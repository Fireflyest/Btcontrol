package com.fireflyest.btcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.fireflyest.btcontrol.util.StatusBarUtil;

public class BatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);

        this.initView();

    }

    private void initView() {

        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_batch);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }


}