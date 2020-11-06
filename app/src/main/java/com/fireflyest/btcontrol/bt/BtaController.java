package com.fireflyest.btcontrol.bt;

import android.content.Context;

import com.fireflyest.btcontrol.bt.callback.ConnectCallback;
import com.fireflyest.btcontrol.bt.callback.OnReceiverCallback;
import com.fireflyest.btcontrol.bt.callback.OnWriteCallback;

public interface BtaController {

    void init(Context context);

    void connect(String type, final int connectionTimeOut, final String devicesAddress, ConnectCallback connectCallback);

    void writeBuffer(byte[] buf, OnWriteCallback writeCallback);

    void closeConnect();

    String getAddress();

    void registerReceiveListener(String requestKey, OnReceiverCallback onReceiverCallback);

    void unregisterReceiveListener(String requestKey);

}
