package com.fireflyest.btcontrol.bean;

public class Bluetooth {

    private String name;

    private short rssi;

    private String address;

    private int statue;

    private int type;

    public Bluetooth(String name, short rssi, String address, int statue, int type) {
        this.name = name;
        this.rssi = rssi;
        this.address = address;
        this.statue = statue;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getRssi() {
        return rssi;
    }

    public void setRssi(short rssi) {
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
