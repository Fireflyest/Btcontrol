package com.fireflyest.btcontrol.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.fireflyest.btcontrol.bt.callback.ConnectCallback;
import com.fireflyest.btcontrol.bt.callback.OnReceiverCallback;
import com.fireflyest.btcontrol.bt.callback.OnWriteCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class NormalController implements BtaController {

    private static NormalController nController;

    private static final String LOG_TAG = "BtController ";

    private UUID uuid = UUID.fromString("d7941ffa-89a9-4c9d-ac2c-95d0f630a1b5");

    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private OnReceiverCallback receiverCallback;

    private String address = "";

    /**
     * 获取NormalController实例对象
     */
    public synchronized static NormalController getInstance() {
        if (null == nController) {
            nController = new NormalController();
        }
        return nController;
    }

    @Override
    public void init(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void connect(String type, int connectionTimeOut, String devicesAddress, ConnectCallback connectCallback) {

        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(devicesAddress);
        if (null == remoteDevice) {
            Log.e(LOG_TAG, "未找到该蓝牙设备 ->" + devicesAddress);
            return;
        }
        if(remoteDevice.getBondState() == 10) remoteDevice.createBond();

//      BOND_NONE 10
//      BOND_BONDING 11
//      BOND_BONDED 12
        Log.e(LOG_TAG, "配对状态 ->" + remoteDevice.getBondState());

        this.address = devicesAddress;

        if(connectThread != null){
            connectThread.cancel();
        }
        connectThread = new ConnectThread(remoteDevice, connectCallback);
        Log.e(LOG_TAG, "正在连接蓝牙设备 ->" + devicesAddress);
        connectThread.start();

    }

    @Override
    public void writeBuffer(byte[] buf, OnWriteCallback writeCallback) {
        connectThread.write(buf, writeCallback);
    }

    @Override
    public void closeConnect() {
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void registerReceiveListener(String requestKey, OnReceiverCallback onReceiverCallback) {
        this.receiverCallback = onReceiverCallback;
    }

    @Override
    public void unregisterReceiveListener(String requestKey) {
        receiverCallback = null;
    }

    private class ConnectThread extends Thread {
        private ConnectCallback connectCallback;
        private BluetoothDevice device;
        private BluetoothSocket socket;
        private InputStream inStream;
        private OutputStream outStream;

        public ConnectThread(BluetoothDevice device, ConnectCallback connectCallback) {
            this.device = device;
            this.connectCallback = connectCallback;

            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                Log.i(LOG_TAG, "Socket's create() method succeed");

            } catch (IOException e) {
                Log.e(LOG_TAG, "Socket's create() method failed", e);
            }

        }

        public void run() {

            bluetoothAdapter.cancelDiscovery();

            try {

                socket.connect();

                connectCallback.onConnSuccess();

            } catch (IOException e) {
                connectCallback.onConnFailed();
                Log.e(LOG_TAG, "Unable to connect");
                // Unable to connect; close the socket and return.
                try {
                    if(socket != null) socket.close();
                } catch (IOException closeException) {
                    Log.e(LOG_TAG, "Could not close the client socket", closeException);
                }
            }

            if(null != socket){
                try {
                    inStream = socket.getInputStream();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error occurred when creating input stream", e);
                }
                try {
                    outStream = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error occurred when creating output stream", e);
                }
            }

            byte[] buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                if(null != socket && !socket.isConnected())continue;
                try {
                    // Read from the InputStream.
                    numBytes = inStream.read(buffer);

                    if(null != receiverCallback && numBytes > 0){
                        receiverCallback.onReceive(buffer);
                    }

                } catch (IOException e) {
                    Log.d(LOG_TAG, "Input stream was disconnected", e);
                    break;
                }
            }

        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes, OnWriteCallback writeCallback) {
            try {
                outStream.write(bytes);

                writeCallback.onSuccess();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error occurred when sending data", e);

                writeCallback.onFailed(OnWriteCallback.FAILED_BLUETOOTH_DISABLE);

            }
        }

        public void cancel() {
            try {
                socket.close();
                Log.e(LOG_TAG, "Closed the client socket");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not close the client socket", e);
            }
        }
    }

}
