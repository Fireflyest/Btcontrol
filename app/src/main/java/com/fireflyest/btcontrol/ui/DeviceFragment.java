package com.fireflyest.btcontrol.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bt.BtController;
import com.fireflyest.btcontrol.bt.BtManager;
import com.fireflyest.btcontrol.bt.callback.ConnectCallback;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.bt.callback.ConnectStateCallback;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.dialog.EditDeviceDialog;
import com.fireflyest.btcontrol.util.AnimateUtil;
import com.fireflyest.btcontrol.util.ReflectUtils;
import com.fireflyest.btcontrol.util.ToastUtil;

import static java.lang.Thread.sleep;

public class DeviceFragment extends Fragment {

    private static final String KEY_ADDRESS = "address";

    private ImageButton connectState;

    private String address;
    private String name;
    private String type;
    private long progress;
    private long create;

    private boolean enable = true;

    private BtController btController;

    private TextView connectButton;

    private ConstraintSet boxConstraintSet = new ConstraintSet();

    public static DeviceFragment newInstance(final String address) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS, address);
        fragment.setArguments(bundle);
        return fragment;
    }


    public DeviceFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(KEY_ADDRESS);
            this.refreshDevice(address);
        }
        btController = BtManager.getBtController();

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                ToastUtil.showShort(view.getContext(), msg.obj.toString());
                return true;
            }
        });

        connectButton = view.findViewById(R.id.main_device_connect);
        TextView editButton = view.findViewById(R.id.main_device_edit);

        TextView addressText = view.findViewById(R.id.main_device_address);
        TextView nameText = view.findViewById(R.id.main_device_name);
        TextView typeText = view.findViewById(R.id.main_device_type);

        ConstraintLayout deviceBox = view.findViewById(R.id.main_device_box);
        boxConstraintSet.clone(deviceBox);

        addressText.setText(address);
        nameText.setText(name);
        typeText.setText(type);

        connectState = view.findViewById(R.id.main_device_state);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                ToastUtil.showShort(getContext(), "连接中...");

                //连接蓝牙
                btController.connect(v.getContext(), type, address, new ConnectStateCallback() {
                    @Override
                    public void connectSucceed(String deviceAddress) {
                        if(!deviceAddress.equals(address))return;
                        handler.obtainMessage(0, "成功连接").sendToTarget();
                        ((AnimatedVectorDrawable)connectState.getDrawable()).start();
                        AnimateUtil.hide(connectButton, 300, 0);

                    }

                    @Override
                    public void connectLost(String deviceAddress) {
                        if(!deviceAddress.equals(address))return;
                        handler.obtainMessage(0, "连接断开").sendToTarget();
                        AnimateUtil.show(connectButton, 300, 0);
                        connectState.setImageResource(R.drawable.animate_connect);
                    }

                });

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity != null){
                    EditDeviceDialog dialog = new EditDeviceDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putLong("progress", progress);
                    bundle.putLong("create", create);
                    dialog.setArguments(bundle);
                    dialog.show(getActivity().getSupportFragmentManager(), "Edit device");
                }
            }
        });

        if(this.isConnect()) {
            ((AnimatedVectorDrawable)connectState.getDrawable()).start();
            connectButton.setVisibility(View.GONE);
            connectButton.setAlpha(0);
        }else {
            if(SettingManager.SELECT_ADDRESS.equals(address) && SettingManager.AUTO_CONNECT){
                connectButton.callOnClick();
            }
        }

        if(enable){
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.item_animation_from_bottom);
            view.startAnimation(animation);
            enable = false;
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {

        this.refreshDevice(address);

        if(this.isConnect()){
            ((AnimatedVectorDrawable)connectState.getDrawable()).start();
            if(connectButton.getVisibility() == View.VISIBLE) AnimateUtil.hide(connectButton, 300, 0);
        }else {
            connectState.setImageResource(R.drawable.animate_connect);
            if(connectButton.getVisibility() == View.GONE) AnimateUtil.show(connectButton, 300, 0);
        }
        super.onResume();
    }


    //------------------------------------------------------------------------

    private Handler handler;

    private boolean isConnect(){
        //蓝牙连接状态
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if(device != null) return ReflectUtils.invokeIs(device, "connected");
        return false;
    }


    private void refreshDevice(final String address){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Device device = DataManager.getInstance().getDeviceDao().findByAddress(address);
                name = device.getName();
                type = device.getType();
                progress = device.getProgress();
                create = device.getCreate();
            }
        }).start();
    }

}