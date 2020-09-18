package com.fireflyest.btcontrol.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Command {

    @PrimaryKey(autoGenerate = true)
    private int id;
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
