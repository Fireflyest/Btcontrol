package com.fireflyest.btcontrol.api.callback;

/**
 * @Description： 连接回调
 */
public interface ConnectCallback {
    /**
     * Notify之后的回调
     */
    void onConnSuccess();

    void onConnFailed();

}
