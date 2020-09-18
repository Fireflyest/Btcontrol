package com.fireflyest.btcontrol.bean;

public class Bluetooth {

    private String name;

    private short rssi;

    private String address;

    private int statue;

    public Bluetooth(String name, short rssi, String address, int statue) {
        this.name = name;
        this.rssi = rssi;
        this.address = address;
        this.statue = statue;
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
}
