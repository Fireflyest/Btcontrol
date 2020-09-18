package com.fireflyest.btcontrol.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fireflyest.btcontrol.bean.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device")
    List<Device> getAll();

    @Query("SELECT * FROM device WHERE address LIKE :address LIMIT 1")
    Device findByAddress(String address);

    @Update
    void updateAll(Device... devices);

    @Insert
    void insert(Device device);

    @Delete
    void delete(Device device);

}
