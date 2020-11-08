package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fireflyest.btcontrol.adapter.BatchList.BatchItemAdapter;
import com.fireflyest.btcontrol.anim.FromRightItemAnimator;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bt.BtManager;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class BatchActivity extends AppCompatActivity {

    private List<Device> devices = new ArrayList<>();

    private static final int ADD_DEVICE = 1;

    private BatchItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);

        this.initView();

    }

    /**
     * 返回
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
            case 0:
            default:
        }
        return true;
    }

    private void initView() {

        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_batch);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        RecyclerView batchList = findViewById(R.id.toolbar_batch_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL);
        batchList.setLayoutManager(layoutManager);
        adapter = new BatchItemAdapter(devices, handler);
        batchList.setAdapter(adapter);
        batchList.setItemAnimator(new FromRightItemAnimator());

        this.refreshDevice(false);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_batch, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void back(){
        finish();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case ADD_DEVICE:
                    Device device = (Device)msg.obj;
                    adapter.addItem(0, device);
                    break;
                default:
            }
            return true;
        }
    });

    private void refreshDevice(final Boolean onlyConnected){
        devices.clear();
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Device> devices = DataManager.getInstance().getDeviceDao().getAll();
                for(Device device : devices){
                    if(!BtManager.getBtController().isConnected(device.getAddress()) && onlyConnected)continue;
                    handler.obtainMessage(ADD_DEVICE, device).sendToTarget();
                }
            }
        }).start();
    }

}