package com.fireflyest.btcontrol.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.fireflyest.btcontrol.bean.Mode;

import java.util.List;

@Dao
public interface ModeDao {

    @Query("SELECT * FROM mode")
    List<Mode> getAll();

    @Query("SELECT * FROM mode WHERE code LIKE :code LIMIT 1")
    Mode findByCode(String code);

    @Insert
    long insert(Mode mode);

    @Delete
    void delete(Mode mode);

}
