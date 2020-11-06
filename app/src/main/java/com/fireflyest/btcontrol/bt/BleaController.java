package com.fireflyest.btcontrol.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fireflyest.btcontrol.bt.callback.ConnectCallback;
import com.fireflyest.btcontrol.bt.callback.OnReceiverCallback;
import com.fireflyest.btcontrol.bt.callback.OnWriteCallback;
import com.fireflyest.btcontrol.bt.request.ReceiverRequestQueue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BleaController implements BtaController {

    private static final String LOG_TAG = "BleController ";

    //BleController实例
    @SuppressLint("StaticFieldLeak")
    private static BleaController sBleManager;
    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic gattCharacteristic;

    private BleGattCallback mGattCallback;
    private OnWriteCallback writeCallback;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //发起连接是否有响应
    private boolean isConnectResponse = false;
    //获取到所有服务的集合
    private Map<String, BluetoothGattCharacteristic> characteristicMap = new HashMap<>();
    //默认连接超时时间:6s
    private static final int CONNECTION_TIME_OUT = 6000;
    //是否是用户手动断开
    private boolean isBreakByMyself = false;
    //接受信息
    private boolean notify;
    //正连接
    private String address = "";
    //连接结果的回调
    private ConnectCallback connectCallback;
    //读操作请求队列
    private ReceiverRequestQueue mReceiverRequestQueue = new ReceiverRequestQueue();
    //接收数据uuid
    private static final UUID BLUETOOTH_NOTIFY_D = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //服务uuid
    private String BLUETOOTH_S;
    //特征uuid
    private String BLUETOOTH_NOTIFY_C;


    //-----------------------------  对外公开的方法 ----------------------------------------------
    /**
     * 获取BleController实例对象
     */
    public synchronized static BleaController getInstance() {
        if (null == sBleManager) {
            sBleManager = new BleaController();
        }
        return sBleManager;
    }

    public void setUUID(String type){
        switch (type){
            case "Ai-Thinker":
                BLUETOOTH_S = "00010203-0405-0607-0809-0a0b0c0d1910";
                BLUETOOTH_NOTIFY_C = "00010203-0405-0607-0809-0a0b0c0d2b10";
                break;
            case "MLT-BT05":
            case "HC-42":
            default:
                BLUETOOTH_S = "0000ffe0-0000-1000-8000-00805f9b34fb";
                BLUETOOTH_NOTIFY_C = "0000ffe1-0000-1000-8000-00805f9b34fb";
                break;
        }
    }

    /**
     * 进行初始化
     * @param context 环境
     */
    @Override
    public void init(Context context) {

        this.enableNotify();

        if (mContext == null) {
            mContext = context;

            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

            mAdapter = mBluetoothManager.getAdapter();

            mGattCallback = new BleGattCallback();
        }
    }

    /**
     * 连接设备
     *
     * @param connectionTimeOut 连接超时时间,默认是6秒.当赋值为0或更小值时用默认值
     * @param devicesAddress    想要连接的设备地址
     */
    @Override
    public void connect(String type, final int connectionTimeOut, final String devicesAddress, ConnectCallback connectCallback) {
        this.setUUID(type);

        BluetoothDevice remoteDevice = mAdapter.getRemoteDevice(devicesAddress);
        if (null == remoteDevice) {
            Log.e(LOG_TAG, "未找到该蓝牙设备 ->" + devicesAddress);
            return;
        }

        this.address = devicesAddress;

        this.connectCallback = connectCallback;

        if (null != mBluetoothGatt) {
            mBluetoothGatt.close();
        }
        this.reset();
        mBluetoothGatt = remoteDevice.connectGatt(mContext, false, mGattCallback);
        Log.e(LOG_TAG, "正在连接蓝牙设备 ->" + devicesAddress);

        this.delayConnectResponse(connectionTimeOut);
    }


    /**
     * 发送数据
     *
     * @param buf 字节
     * @param writeCallback 回调
     */
    @Override
    public void writeBuffer(byte[] buf, OnWriteCallback writeCallback) {
        this.writeCallback = writeCallback;
        if (isEnable()) {
            writeCallback.onFailed(OnWriteCallback.FAILED_BLUETOOTH_DISABLE);
            Log.e(LOG_TAG, "FAILED_BLUETOOTH_DISABLE");
            return;
        }

        if (gattCharacteristic == null) {
            gattCharacteristic = getBluetoothGattCharacteristic(BLUETOOTH_NOTIFY_C);
        }

        if (null == gattCharacteristic) {
            writeCallback.onFailed(OnWriteCallback.FAILED_INVALID_CHARACTER);
            Log.e(LOG_TAG, "FAILED_INVALID_CHARACTER");
            return;
        }

        //设置数组进去
        gattCharacteristic.setValue(buf);

        //发送
        boolean b = mBluetoothGatt.writeCharacteristic(gattCharacteristic);

        Log.e(LOG_TAG, "send:" + b + "data：" + bytesToHexString(buf));
    }

    /**
     * 设置读取数据的监听
     *
     * @param requestKey 请求码
     * @param onReceiverCallback 回调
     */
    @Override
    public void registerReceiveListener(String requestKey, OnReceiverCallback onReceiverCallback) {
        mReceiverRequestQueue.set(requestKey, onReceiverCallback);
    }

    /**
     * 移除读取数据的监听
     *
     * @param requestKey 请求码
     */
    @Override
    public void unregisterReceiveListener(String requestKey) {
        mReceiverRequestQueue.removeRequest(requestKey);
    }

    /**
     * 手动断开Ble连接
     */
    @Override
    public void closeConnect() {
        disConnection();
        isBreakByMyself = true;
        if(null != mBluetoothGatt){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        gattCharacteristic = null;
        mBluetoothManager = null;
        address = "";
    }

    public void enableNotify() {
        this.notify = true;
    }

    @Override
    public String getAddress() {
        return address;
    }

    //----------------------------------  私有方法 ----------------------------------------------


    /**
     * 将byte数组转为16进制字符串 此方法主要目的为方便Log的显示
     */
    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase()).append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 当前蓝牙是否打开
     */
    private boolean isEnable() {
        if (null != mAdapter) {
            return !mAdapter.isEnabled();
        }
        return true;
    }


    /**
     * 复位
     */
    private void reset() {
        isConnectResponse = false;
        characteristicMap.clear();
    }

    /**
     * 如果连接connectionTimeOut时间后还没有响应,手动关掉连接.
     *
     * @param connectionTimeOut 连接时间
     */
    private void delayConnectResponse(int connectionTimeOut) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnectResponse && !isBreakByMyself) {
                    Log.e(LOG_TAG, "蓝牙连接超时");
                    disConnection();
                    reConnect();
                } else {
                    isBreakByMyself = false;
                }
            }
        }, connectionTimeOut <= 0 ? CONNECTION_TIME_OUT : connectionTimeOut);
    }


    /**
     * 断开连接
     */
    private void disConnection() {
        if (null == mAdapter || null == mBluetoothGatt) {
            Log.e(LOG_TAG, "disconnection error maybe no init");
            return;
        }
        mBluetoothGatt.disconnect();
        reset();
    }


    /**
     * 蓝牙GATT连接及操作事件回调
     */
    private class BleGattCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                isBreakByMyself = false;
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //断开连接
                if (!isBreakByMyself) {
                    reConnect();
                }
                reset();
            }
        }

        //服务被发现了
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (null == mBluetoothGatt || status != BluetoothGatt.GATT_SUCCESS) return;

            for(BluetoothGattService service : mBluetoothGatt.getServices()){
                Log.e(LOG_TAG, "\n--------------------------------------------"+"\n服务:"+service.getUuid() +"\n服务类型:"+service.getType()+"\n服务特征:"+service.getCharacteristics().size());
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                    Log.e(LOG_TAG, "\n特征:"+characteristic.getUuid()+"\nProperties:"+ characteristic.getProperties());
                }
                Log.e(LOG_TAG, "-");
            }

            BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(BLUETOOTH_S));
            //遍历特征
            for(BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()){
                characteristicMap.put(characteristic.getUuid().toString(), characteristic);
                if(!characteristic.getUuid().toString().equals(BLUETOOTH_NOTIFY_C))continue;
                if (enableNotification(notify, characteristic)) {
                    isConnectResponse = true;
                    connSuccess();
                } else {
                    reConnect();
                }
            }

        }

        //收到数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

//            if (null != mReceiverRequestQueue) {
                Log.e(LOG_TAG, "收到数据: "+ Arrays.toString(characteristic.getValue()));
                HashMap<String, OnReceiverCallback> map = mReceiverRequestQueue.getMap();
                final byte[] rec = characteristic.getValue();
                for (final OnReceiverCallback onReceiverCallback: map.values()){
                    if(onReceiverCallback == null)continue;
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            onReceiverCallback.onReceive(rec);
                        }
                    });
                }
//                for (String key : mReceiverRequestQueue.getMap().keySet()) {
//                    final OnReceiverCallback onReceiverCallback = map.get(key);
//                    runOnMainThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            onReceiverCallback.onReceive(rec);
//                        }
//                    });
//                }
//            }
        }

        //描述符被写了
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        }

        //描述符被读了
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        }

        //发送数据结果
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (null != writeCallback) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onSuccess();
                        }
                    });
                    Log.e(LOG_TAG, "Send data success!");
                } else {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onFailed(OnWriteCallback.FAILED_OPERATION);
                        }
                    });
                    Log.e(LOG_TAG, "Send data failed!");
                }
            }
        }
    }


    private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null || characteristic == null){
            return false;
        }
        if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)){
            return false;
        }

        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(BLUETOOTH_NOTIFY_D);

        if (clientConfig == null){
            return false;
        }

        if (enable) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        return mBluetoothGatt.writeDescriptor(clientConfig);
    }


    /**
     * 根据服务UUID和特征UUID,获取一个特征{@link BluetoothGattCharacteristic}
     *
     * @param characterUUID 特征UUID
     */
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(String characterUUID) {
        if (isEnable()) {
            throw new IllegalArgumentException(" Bluetooth is no enable please call BluetoothAdapter.enable()");
        }
        if (null == mBluetoothGatt) {
            Log.e(LOG_TAG, "mBluetoothGatt is null");
            return null;
        }

        //找服务
        if (null == characteristicMap) {
            Log.e(LOG_TAG, "Not found the serviceUUID!");
            return null;
        }

        //找特征
        BluetoothGattCharacteristic gattCharacteristic = null;

        for (Map.Entry<String, BluetoothGattCharacteristic> entry : characteristicMap.entrySet()) {

            if (characterUUID.equals(entry.getKey())) {
                gattCharacteristic = entry.getValue();
                break;
            }
        }
        return gattCharacteristic;
    }

    private void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    //此方法断开连接或连接失败时会被调用。可在此处理自动重连,内部代码可自行修改，如发送广播
    private void reConnect() {
        if(connectCallback != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    connectCallback.onConnFailed();
                }
            });
        }

        Log.e(LOG_TAG, "Ble disconnect or connect failed!");
    }

    //此方法Notify成功时会被调用。可在通知界面连接成功,内部代码可自行修改，如发送广播
    private void connSuccess() {
        if(connectCallback != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    connectCallback.onConnSuccess();
                }
            });
        }
        Log.e(LOG_TAG, "Ble connect success!");
    }

}
