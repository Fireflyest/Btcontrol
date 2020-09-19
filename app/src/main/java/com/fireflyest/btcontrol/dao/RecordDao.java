package com.fireflyest.btcontrol.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.fireflyest.btcontrol.bean.Record;

import java.util.List;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM record")
    List<Record> getAll();

    @Query("SELECT * FROM record WHERE id IN (:id)")
    List<Record> loadAllByIds(int[] id);

    @Query("SELECT * FROM record WHERE time > :time LIMIT 20")
    List<Record> findByTime(long time);

    @Query("SELECT * FROM record WHERE address like :address LIMIT 20")
    List<Record> findByAddress(String address);

    @Insert
    void insertAll(Record... records);

    @Delete
    void delete(Record record);

}
