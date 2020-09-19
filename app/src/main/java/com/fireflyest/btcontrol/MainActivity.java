package com.fireflyest.btcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.fireflyest.btcontrol.adapter.CardAdapter;
import com.fireflyest.btcontrol.adapter.IndexList.IndexItemAdapter;
import com.fireflyest.btcontrol.adapter.PagerAdapter;
import com.fireflyest.btcontrol.api.BleController;
import com.fireflyest.btcontrol.api.callback.ConnectCallback;
import com.fireflyest.btcontrol.api.callback.OnWriteCallback;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bean.Index;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.dialog.EditDeviceDialog;
import com.fireflyest.btcontrol.ui.BlankFragment;
import com.fireflyest.btcontrol.ui.ControlFragment;
import com.fireflyest.btcontrol.ui.DeviceFragment;
import com.fireflyest.btcontrol.ui.InfoFragment;
import com.fireflyest.btcontrol.util.CalendarUtil;
import com.fireflyest.btcontrol.util.StatusBarUtil;
import com.fireflyest.btcontrol.util.ToastUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements EditDeviceDialog.NoticeDialogListener {

    private final List<Fragment> deviceList = new ArrayList<>();
    private final List<Fragment> pagerList = new ArrayList<>();

    private ImageButton actionButton;
    private DrawerLayout drawerLayout;
    private ViewPager pagerCards;
    private ViewPager deviceCards;

    private BleController bleController;

    private Map<String, Device> deviceMap = new HashMap<>();
    private List<Index> indices = new ArrayList<>();
    private int deviceIndex = 0;

    private CardAdapter deviceAdapter;
    private PagerAdapter pagerAdapter;
    private IndexItemAdapter indexItemAdapter;

    private SettingManager settingManager;
    private DataManager dataManager;

    //启动页面请求码
    public static final int REQUEST_BLUETOOTH = 2;
    public static final int REQUEST_MODE = 3;
    public static final int REQUEST_COMMAND = 4;

    public static final int REFRESH_CARDS = 6;
    public static final int REFRESH_PAGER = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initData();
        //初始化蓝牙控制器
        this.initBleController();
        //初始化界面
        this.initView();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.nav_bluetooth:
                intent = new Intent(this, ConnectActivity.class);
                this.startActivityForResult(intent, REQUEST_BLUETOOTH);
                break;
            case R.id.nav_alarm:
//                intent = new Intent(this, AlarmActivity.class);
//                this.startActivityForResult(intent, REQUEST_COMMAND);
                break;
            default:
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        if(data == null)return;
        if(resultCode != RESULT_OK)return;
        switch (requestCode){
            case REQUEST_BLUETOOTH:
                String name = data.getStringExtra("name");
                String address = data.getStringExtra("address");
                this.addDevice(name, address);
                break;
            case REQUEST_MODE:
            case REQUEST_COMMAND:
                final String mode = data.getStringExtra("mode");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Device device = deviceMap.get(SettingManager.SELECT_ADDRESS);
                        if(device != null){
                            if(mode != null && !mode.equals(SettingManager.CLOSE_CODE)){
                                device.setMode(Integer.parseInt(mode));
                                dataManager.getDeviceDao().updateAll(device);
                                settingManager.setStringPreference("select_address", "none");
                                settingManager.setStringPreference("select_address", device.getAddress());
                            }else {
                                device.setOpen(false);
                                dataManager.getDeviceDao().updateAll(device);
                                actionButton.setImageResource(R.drawable.animate_action);
                            }
                        }
                    }
                }).start();
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(deviceMap.size() != 0)return;
                List<Device> devices = dataManager.getDeviceDao().getAll();
                String address = SettingManager.SELECT_ADDRESS;
                for(Device device : devices){
                    addDeviceCard(device);
                    if(address.equals(device.getAddress())){
                        pagerCards.setCurrentItem(devices.indexOf(device));
                        if(device.isOpen()){
                            actionButton.setImageResource(R.drawable.animate_stop);
                        }else {
                            actionButton.setImageResource(R.drawable.animate_action);
                        }
                    }
                }
            }
        }).start();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        settingManager.unRegister();
        super.onDestroy();
    }

    @Override
    public void onDialogDoneClick(String name, long progress) {
        final Device device = this.getSelectDevice();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataManager.getDeviceDao().updateAll(device);
                handler.obtainMessage(REFRESH_CARDS).sendToTarget();
            }
        }).start();

        device.setName(name);
        device.setProgress(progress);
        deviceMap.put(device.getAddress(), device);
        ToastUtil.showShort(MainActivity.this, "设备已修改");
        this.getSupportFragmentManager()
                .beginTransaction()
                .remove(deviceAdapter.getItem(deviceIndex))
                .commit();
        deviceList.remove(deviceIndex);
        deviceList.add(deviceIndex, DeviceFragment.newInstance(device.getAddress()));
    }

    @Override
    public void onDialogDeleteClick() {
        indexItemAdapter.removeItem(deviceIndex);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataManager.getDeviceDao().delete(deviceMap.get(SettingManager.SELECT_ADDRESS));
                deviceMap.remove(SettingManager.SELECT_ADDRESS);
                settingManager.setStringPreference("select_address", "none");
            }
        }).start();
        deviceList.remove(deviceIndex);
        handler.obtainMessage(REFRESH_CARDS).sendToTarget();
    }


    //---------------------------------------------------------------------------------------



    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case REFRESH_CARDS:
                    deviceAdapter.notifyDataSetChanged();
                    break;
                case REFRESH_PAGER:
                    pagerAdapter.notifyDataSetChanged();
                    break;
                default:
            }
            return true;
        }
    });


    /**
     * 初始化界面
     */
    private void initView(){
        StatusBarUtil.StatusBarLightMode(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        actionButton = findViewById(R.id.main_action);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device device = getSelectDevice();
                if(getSelectDevice().isOpen()){
                    actionButton.setImageResource(R.drawable.animate_stop);
                    sendCommand(SettingManager.CLOSE_CODE);
                }else {
                    actionButton.setImageResource(R.drawable.animate_action);
                    sendCommand(String.valueOf(device.getMode()));
                }
            }
        });

        drawerLayout = findViewById(R.id.main_drawer);

        RecyclerView indexList = findViewById(R.id.main_index);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        indexList.setLayoutManager(manager);
        indexItemAdapter = new IndexItemAdapter(indices, handler);
        indexList.setAdapter(indexItemAdapter);

        pagerCards = findViewById(R.id.main_pager);
        pagerList.add(new InfoFragment());
        pagerList.add(new ControlFragment());
        pagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), pagerList);
        pagerCards.setAdapter(pagerAdapter);
        TabLayout pagerTable = findViewById(R.id.main_table);
        pagerTable.setupWithViewPager(pagerCards);

        deviceCards = findViewById(R.id.toolbar_viewpager);
        deviceCards.setOffscreenPageLimit(1);
        if(deviceList.size() == 0) deviceList.add(new BlankFragment());
        deviceAdapter = new CardAdapter(this.getSupportFragmentManager(), deviceList);
        deviceCards.setAdapter(deviceAdapter);
        deviceCards.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int page = 0;
            private int to = 0;
            private float offset = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(positionOffset == 0){
                    //切换时刷新页面
                    if(position != page){
                        handler.obtainMessage(REFRESH_CARDS).sendToTarget();
                        page = position;
                        offset = 0;
                    }

                    //判断是否回滑
                    if(to > page){
                        indexItemAdapter.moveItem(to, to-1);
                        to = to - 1;
                        offset = 0;
                    }else if(to < page){
                        indexItemAdapter.moveItem(to, to+1);
                        to = to + 1;
                        offset = 0;
                    }
                }

                //切换页面小点
                if(offset == 0) {
                    offset = positionOffset;
                    if (offset != 0) {
                        to = page + (offset < 0.5 ? 1 : -1);
                        indexItemAdapter.moveItem(page, to);
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
                if(position < deviceMap.values().size()){
                    Device device = (Device)deviceMap.values().toArray()[deviceIndex];
                    settingManager.setStringPreference("select_address", device.getAddress());
                    if(device.isOpen()){
                        actionButton.setImageResource(R.drawable.animate_stop);
                    }else {
                        actionButton.setImageResource(R.drawable.animate_action);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 获取数据
     */
    private void initData(){
        dataManager = DataManager.getInstance();
        settingManager = SettingManager.getInstance();
    }

    /**
     * 连接
     * @param name 蓝牙类型
     * @param address 蓝牙地址
     */
    private void addDevice(String name, String address){

        if(deviceMap.containsKey(address)){
            ToastUtil.showShort(MainActivity.this, "该设备已存在");
            return;
        }

        final Device device = new Device();
        device.setAddress(address);
        device.setType(name);
        device.setName("未命名设备");
        device.setCreate(CalendarUtil.getDate());
        device.setOpen(false);
        device.setMode(301);

        //关闭原有连接
        bleController.closeBleConnect();
        //连接蓝牙
        bleController.connect(device.getType(), 0, address, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                ToastUtil.showShort(MainActivity.this, "成功添加蓝牙");

                addDeviceCard(device);

                //关闭连接
                bleController.closeBleConnect();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataManager.getDeviceDao().insert(device);
                    }
                }).start();

                if("none".equals(SettingManager.SELECT_ADDRESS)){
                    settingManager.setStringPreference("select_address", device.getAddress());
                }

            }

            @Override
            public void onConnFailed() {
                ToastUtil.showShort(MainActivity.this, "无法添加蓝牙");
            }
        });
    }

    /**
     * 发送蓝牙指令
     * @param command 指令
     */
    private void sendCommand(final String command){
        byte[] bytes = (command).getBytes();
        bleController.writeBuffer(bytes, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                if(SettingManager.CLOSE_CODE.equals(command)){
                    ToastUtil.showShort(getBaseContext(), "已关闭");
                    ((AnimatedVectorDrawable)actionButton.getDrawable()).start();
                    getSelectDevice().setOpen(false);
                }else {
                    ToastUtil.showShort(getBaseContext(), "已开启");
                    ((AnimatedVectorDrawable)actionButton.getDrawable()).start();
                    getSelectDevice().setOpen(true);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataManager.getDeviceDao().updateAll(getSelectDevice());
                    }
                }).start();
            }

            @Override
            public void onFailed(int state) {
                ToastUtil.showShort(getBaseContext(), "切换失败");
            }
        });
    }

    /**
     * 获取当前选中设备
     * @return 设备
     */
    private Device getSelectDevice(){
        return (Device)deviceMap.values().toArray()[deviceIndex];
    }

    /**
     * 添加设备卡片
     * @param device 设备
     */
    private void addDeviceCard(Device device){
        if(deviceList.size() == 1 && deviceList.get(0) instanceof BlankFragment)deviceList.clear();

        deviceList.add(DeviceFragment.newInstance(device.getAddress()));

        deviceMap.put(device.getAddress(), device);

        Index index = new Index();
        index.setSelect(indexItemAdapter.getItemCount() == 0);
        indexItemAdapter.addItem(index);

        handler.obtainMessage(REFRESH_CARDS).sendToTarget();

    }

    /**
     * 初始化蓝牙控制器
     */
    private void initBleController(){
        bleController = BleController.getInstance();
        //初始化蓝牙适配器
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();
    }

}