package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireflyest.btcontrol.adapter.BluetoothList.BluetoothItemAdapter;
import com.fireflyest.btcontrol.api.BleController;
import com.fireflyest.btcontrol.bean.Bluetooth;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.ReflectUtils;
import com.fireflyest.btcontrol.util.StatusBarUtil;
import com.fireflyest.btcontrol.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothItemAdapter itemAdapter;

    private TextView name;
    private TextView address;
    private ConstraintLayout loading;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Bluetooth> bluetooths = new ArrayList<>();

    private Bluetooth backBluetooth;

    public static final int UPDATE_CONNECTION = 1;
    public static final int START_CONNECTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        this.initData();

        this.initBluetooth();

        this.initView();

        this.refreshConnected();

        this.startScanBluetooth();
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

    @Override
    public void onBackPressed() {
        back();
    }



    private void initData(){
    }

    private void initView(){

        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_connect);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        name = findViewById(R.id.connect_connected_name);
        address = findViewById(R.id.connect_connected_address);
        loading = findViewById(R.id.toolbar_connect_scan);

        swipeRefreshLayout = findViewById(R.id.connect_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startScanBluetooth();
            }
        });

        ConstraintLayout connectBox = findViewById(R.id.connect_connected_card);
        connectBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BleController.getInstance().closeBleConnect();
                ToastUtil.showShort(ConnectActivity.this,  "已断开连接");
                refreshConnected();
                return true;
            }
        });

        RecyclerView bluetoothList = findViewById((R.id.connect_bluetooth_list));
        bluetoothList.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new BluetoothItemAdapter(bluetooths, handler);
        bluetoothList.setAdapter(itemAdapter);

        //刷新按钮
        ImageButton refresh = findViewById(R.id.connect_connected_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshConnected();
            }
        });

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                case UPDATE_CONNECTION:
                    refreshConnected();
                    break;
                case START_CONNECTION:
                    backBluetooth = (Bluetooth) msg.obj;
                    back();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    /**
     * 搜索蓝牙设备
     */
    private synchronized void startScanBluetooth() {

        loading.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(false);

        bluetooths.clear();
        itemAdapter.notifyDataSetChanged();

        if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
        bluetooths.clear();
        bluetoothAdapter.startDiscovery();
        //注册广播接收
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        ToastUtil.showShort(this,  "开始扫描");
    }

    /**
     * 刷新已连接的设备
     */
    private void refreshConnected(){
        //获取全部配对的列表
        name.setText(R.string.connect_connected_name);
        address.setText(R.string.connect_connected_address);
        String connecting = BleController.getInstance().getAddress();
        if(TextUtils.isEmpty(connecting))return;
        BluetoothDevice device= bluetoothAdapter.getRemoteDevice(connecting);
        if(device == null)return;
        if(!isConnectedDevice(device))return;
        name.setText(device.getName());
        address.setText(device.getAddress());
    }

    /**
     * 接收蓝牙广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device == null)return;
                if(TextUtils.isEmpty(device.getName()))return;
                if(SettingManager.AUTO_DISCERN
                        && !device.getName().contains("Ai-Thinker")
                        && !device.getName().contains("BT05-A")
                ) return;

                short rssi = -9999;
                Bundle bundle = intent.getExtras();
                if(bundle != null)rssi = bundle.getShort(BluetoothDevice.EXTRA_RSSI);

                itemAdapter.addItem(new Bluetooth(device.getName(), rssi, device.getAddress(), device.getBondState()));

                handler.obtainMessage(UPDATE_CONNECTION).sendToTarget();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//找完
                loading.setVisibility(View.GONE);
            }

        }

    };

    private void initBluetooth(){
        //初始化蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            ToastUtil.showShort(this, "蓝牙未开启");
            bluetoothAdapter.enable();
        }
    }

    private void back(){
        Intent intent = new Intent();
        if(null != backBluetooth){
            intent.putExtra("name", backBluetooth.getName());
            intent.putExtra("address", backBluetooth.getAddress());
            setResult(RESULT_OK, intent);
        }else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    /**
     * 判断该设备是否连接
     * @param device 设备
     * @return 是否连接
     */
    private boolean isConnectedDevice(BluetoothDevice device) {
        if (device == null) return false;
        return ReflectUtils.invokeIs(device, "connected");
    }

}