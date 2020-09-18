package com.fireflyest.btcontrol.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 模式
 * 计划：
 * 1、 一个设备对应一个模式列表
 * 2、 由设备控制所需时长
 */
@Entity
public class Mode {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;

    private String code;

    private String desc;

    public Mode(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
