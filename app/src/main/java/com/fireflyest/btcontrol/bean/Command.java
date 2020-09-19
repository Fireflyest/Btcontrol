package com.fireflyest.btcontrol.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 指令发送与接收记录
 */
@Entity
public class Command {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String address;
    private long time;
    private String type;
    private String text;
    private boolean success;

    public Command() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
