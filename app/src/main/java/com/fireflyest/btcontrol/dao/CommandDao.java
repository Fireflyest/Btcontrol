package com.fireflyest.btcontrol.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.fireflyest.btcontrol.bean.Command;
import java.util.List;

@Dao
public interface CommandDao {

    @Query("SELECT * FROM command")
    List<Command> getAll();

    @Query("SELECT * FROM command WHERE id IN (:id)")
    List<Command> loadAllByIds(int[] id);

    @Query("SELECT * FROM command WHERE time > :time LIMIT 20")
    List<Command> findByTime(long time);

    @Query("SELECT * FROM command WHERE address like :address LIMIT 20")
    List<Command> findByAddress(String address);

    @Insert
    void insertAll(Command... command);

    @Delete
    void delete(Command command);

}
