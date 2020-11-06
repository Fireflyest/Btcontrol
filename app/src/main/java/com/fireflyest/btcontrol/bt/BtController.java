package com.fireflyest.btcontrol.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.fireflyest.btcontrol.bt.callback.ConnectStateCallback;
import com.fireflyest.btcontrol.bt.callback.OnReceiverCallback;
import com.fireflyest.btcontrol.bt.callback.OnWriteCallback;

import java.util.List;

public interface BtController {

    void init(BluetoothManager bluetoothManager);

    void connect(Context context, String type, final String address, ConnectStateCallback connectStateCallback);

    void writeBuffer(String address, byte[] buf, OnWriteCallback writeCallback);

    void closeConnect(String address);

    boolean isConnected(String address);

    List<BluetoothDevice> getDeviceList();

    BluetoothGatt getGatt(String address);

    void registerReceiveListener(String requestKey, OnReceiverCallback onReceiverCallback);

    void unregisterReceiveListener(String requestKey);

}
