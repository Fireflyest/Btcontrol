package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.fireflyest.btcontrol.api.BleController;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.StatusBarUtil;
import com.fireflyest.btcontrol.util.ToastUtil;

import static java.lang.Thread.sleep;

public class LaunchActivity extends AppCompatActivity {

    //申请权限
    public static final int REQUEST_ENABLE_BT = 1;

    //开启主界面
    public static final int START_ACTIVITY = 2;

    //开始动画
    public static final int START_ANIMATION = 3;

    private ConstraintLayout launchBox;
    private TextView launchName;

    private boolean permission = false;

    private ConstraintSet launchSet = new ConstraintSet();

    private Transition transition = new AutoTransition().setDuration(300).setStartDelay(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        StatusBarUtil.StatusBarLightMode(this);

        //初始化数据
        DataManager.getInstance().initDataManager(this);

        //初始化设置
        SettingManager.getInstance().initSettingManager(
                PreferenceManager.getDefaultSharedPreferences(this)
        );

        //初始化蓝牙控制器
        BleController.getInstance().init(this);

        //申请权限
        this.requestPermission();

        launchBox = findViewById(R.id.launch_box);
        launchSet.clone(launchBox);

        launchName = findViewById(R.id.launch_name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //已有权限直接开启主界面
                    handler.obtainMessage(START_ANIMATION).sendToTarget();
                    sleep(800);
                    if(permission) handler.obtainMessage(START_ACTIVITY).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //蓝牙权限开启失败
                ToastUtil.showLong(this, "未开启权限");
                finish();
            }else {
                handler.obtainMessage(START_ACTIVITY).sendToTarget();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == START_ACTIVITY){
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
            }else if (msg.what == START_ANIMATION){
                TransitionManager.beginDelayedTransition(launchBox, transition);
                launchName.setVisibility(View.VISIBLE);
                launchSet.setVisibility(R.id.launch_logo, ConstraintSet.VISIBLE);
                launchSet.applyTo(launchBox);

            }
            return true;
        }
    });


    /**
     * 动态申请权限
     */
    private void requestPermission(){
        int state = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(state == PackageManager.PERMISSION_GRANTED){
            permission = true;
        }else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_ENABLE_BT
            );
        }
    }


}