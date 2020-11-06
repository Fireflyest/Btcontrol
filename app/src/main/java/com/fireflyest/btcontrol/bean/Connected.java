package com.fireflyest.btcontrol.bean;

public class Connected {

    private String name;

    private String address;

    private short rssi;

    private boolean enable;

    public Connected(String name, String address, boolean enable) {
        this.name = name;
        this.address = address;
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
