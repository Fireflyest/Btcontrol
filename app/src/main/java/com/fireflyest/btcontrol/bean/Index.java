package com.fireflyest.btcontrol.bean;

/**
 * 设备滑动卡片的小点，不存储
 */
public class Index {

    private boolean select;

    private String address;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
